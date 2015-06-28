package de.aksw.surface

import java.io.{File, FileFilter}

import com.gilt.lucene.LuceneFieldHelpers._
import com.gilt.lucene._
import com.hp.hpl.jena.rdf.model.{Property, Resource}
import de.aksw.iterator.ExtendedIteratorStream
import org.apache.jena.riot.RDFDataMgr
import org.apache.lucene.document.{Document, Field, FieldType, StringField}
import org.apache.lucene.index.{FieldInfo, Term}
import org.apache.lucene.search.BooleanClause.Occur
import org.apache.lucene.search._

import scala.collection.immutable.ListMap

trait SpecifiableLuceneIndexPathProvider extends LuceneIndexPathProvider {
    val path : File
    override def withIndexPath[T](f: (File) => T): T = {
       require(path.isDirectory() || path.mkdirs())
       f(path)
    }
}

object SurfaceFormIndexer {

  val LABEL : String = "label"
  val URINAME: String = "uriName"
  val URI: String = "uri"

  val TIMES_MORE_RESULTS: Int = 10

  def indexExists(into : File) = {
    val files = into.listFiles(new FileFilter(){
      override def accept(pathname : File) = {
        pathname.getName equals "segments.gen" 
      }
    })
    files != null && files.length == 1
  }
}

/**
 * @author dhaeb
 */
class SurfaceFormIndexer(indexable : File, into : File) {
  import SurfaceFormIndexer._
  
  val indexManager = indexIfNeeded(into)

  private def indexIfNeeded(into : File) = {
    val indexManager = new ReadableLuceneIndex
    with WritableLuceneIndex
    with LuceneStandardAnalyzer
    with FSLuceneDirectory
    with SimpleFSLuceneDirectoryCreator
    with SpecifiableLuceneIndexPathProvider {val path = into}
    if(! indexExists(into)){
      require(indexable.exists() && indexable.isFile())
    	  def indexTurtleDocuments() = {
    		  val documents : Iterable[Document] = parseFile()
          indexManager.addDocuments(documents)
    	  }
    	  indexTurtleDocuments()
      }
    indexManager
  }

  import scala.collection.JavaConversions._

  private def createFieldType: FieldType = {
    val myStringType = new FieldType(StringField.TYPE_STORED);
    myStringType.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS)
    myStringType.setOmitNorms(false);
    myStringType.setTokenized(true)
    myStringType
  }

  def parseFile() = {
    val model = RDFDataMgr.loadModel(indexable.getAbsolutePath)
    val stream : Stream[Resource] = ExtendedIteratorStream(model.listSubjects())()
    val dcat = "http://www.w3.org/2004/02/skos/core#altLabel";
    val p : Property  = model.createProperty(dcat);
    val req = for {
      subject <- stream.toSeq.par
      obj <- model.listObjectsOfProperty(subject, p)
    } yield {
      def preprocessString(s : Any) = s.toString().toLowerCase()
      val returnable = new Document()
      returnable.addStoredOnlyField(URI, subject.getURI)
      val myStringType: FieldType = createFieldType
      val uriNameField: Field = new Field(URINAME, preprocessString(subject.getLocalName), myStringType)
      uriNameField.setBoost(0.9f)
      returnable.add(uriNameField)
      returnable.add(new Field(LABEL, preprocessString(obj), myStringType))
      returnable
    }
    req.seq
  }

  def query(queryString : String, resultSize : Int = 10) : Iterable[Document] = {
    val query = createQuery(queryString)
    val topDocuments: Iterable[Document] = indexManager.searchTopDocuments(query, resultSize * TIMES_MORE_RESULTS) // get more results than needed to be able to drop some due to be not unique --> the approch doeing this using lucene is slow!
    topDocuments.foldLeft(ListMap[String, Document]()){ (acc : ListMap[String, Document], e : Document) => // filter out duplicate uris
      val uri: String = e.get(URI)
      if(acc.contains(uri)){
        acc
      } else {
        acc.updated(uri, e)
      }
    }.values.take(resultSize)
  }

  def createQuery(queryString: String): Query = {
    val query: BooleanQuery = new BooleanQuery() // the final query, we aggregate different query types to get good results
    val phraseQuery: PhraseQuery = new PhraseQuery() // for concated terms, e.g. "java platform"

    def createStartsWithQuery(t: String): Unit = {
      val trimedInput: String = t.trim
      if (!trimedInput.isEmpty && trimedInput != "*") {
        val parse: Query = new PrefixQuery(new Term(LABEL, trimedInput))
        parse.setBoost(0.9f) // other (exact) matches should be more important
        val clause: BooleanClause = new BooleanClause(parse, Occur.SHOULD)
        query.add(clause)
      }
    }
    queryString.toLowerCase.split("\\s").foreach({ t =>
      phraseQuery.add(new Term(LABEL, t)) // add term to phrase query (TODO this may be improved using a proper query parser, which creates PhraseQueries ootb)
      createStartsWithQuery(t) // startsWith query for current word
    })
    phraseQuery.setBoost(1.1f) // pharse query is important
    query.add(new BooleanClause(phraseQuery, Occur.SHOULD))
    query
  }
}