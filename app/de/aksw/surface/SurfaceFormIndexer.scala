package de.aksw.surface

import java.io.{File, FileFilter}

import com.gilt.lucene.LuceneFieldHelpers._
import com.gilt.lucene._
import com.hp.hpl.jena.rdf.model.{NodeIterator, Property, ResIterator, Resource}
import org.apache.jena.riot.RDFDataMgr
import org.apache.lucene.analysis.core.KeywordAnalyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Field.Store
import org.apache.lucene.document.{FieldType, Field, StringField, Document}
import org.apache.lucene.index.{FieldInfo, Term}
import org.apache.lucene.queryparser.classic.{MultiFieldQueryParser, QueryParser}
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser
import org.apache.lucene.search.BooleanClause.Occur
import org.apache.lucene.search._

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
  
  require(indexable.exists() && indexable.isFile())

  val indexManager = indexIfNeeded(into)

  private def indexIfNeeded(into : File) = {
    val indexManager = new ReadableLuceneIndex
    with WritableLuceneIndex
    with LuceneStandardAnalyzer
    with FSLuceneDirectory
    with SimpleFSLuceneDirectoryCreator
    with SpecifiableLuceneIndexPathProvider {val path = into}
    if(! indexExists(into)){
    	  def indexTurtleDocuments() = {
    		  val documents : Iterable[Document] = parseFile()
          indexManager.addDocuments(documents)
    	  }
    	  indexTurtleDocuments()
      }
    indexManager
  }

  object ResourceStream {
    def apply(it :ResIterator)(): Stream[Resource] =  {
      if(it.hasNext()){
        new ResourceStream(it)
      } else {
        Stream.Empty
      }
    }
  }
    
  class ResourceStream(private val it : ResIterator) extends Stream[Resource] {
    val res = it.next()
    override def isEmpty = false
    override def head = res
    override def tail = ResourceStream.apply(it)()
    def tailDefined = true
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
    val stream : Stream[Resource] = ResourceStream(model.listSubjects())()
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

  val qp = indexManager.queryParserForDefaultField(LABEL)

  def query(queryString : String, resultSize : Int = 10) = {
    val query: BooleanQuery = new BooleanQuery() // the final query, we aggregate different query types to get good results
    val phraseQuery: PhraseQuery = new PhraseQuery() // for concated terms, e.g. "java platform"

    def createStartsWithQuery(t: String): Unit = {
      val parse: Query = qp.parse(t + "*")
      parse.setBoost(0.9f) // other (exact) matches should be more important
      val clause: BooleanClause = new BooleanClause(parse, Occur.SHOULD)
      query.add(clause)
    }
    queryString.toLowerCase.split(" ").foreach({t =>
        phraseQuery.add(new Term(LABEL, t)) // add term to phrase query (TODO this may be improved using a proper query parser, which creates PhraseQueries ootb)
        createStartsWithQuery(t)  // startsWith query for current word
    })
    phraseQuery.setBoost(1.1f) // pharse query is important
    query.add(new BooleanClause(phraseQuery, Occur.SHOULD))
    indexManager.searchTopDocuments(query,resultSize)
  }
  
}