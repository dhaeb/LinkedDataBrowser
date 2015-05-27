package de.aksw.surface

import java.io.File
import org.apache.jena.riot.RDFDataMgr
import com.fasterxml.jackson.databind.util.ViewMatcher.Empty
import com.gilt.lucene.FSLuceneDirectory
import com.gilt.lucene.LuceneIndexPathProvider
import com.gilt.lucene.LuceneStandardAnalyzer
import com.gilt.lucene.RamLuceneDirectory
import com.gilt.lucene.ReadableLuceneIndex
import com.gilt.lucene.SimpleFSLuceneDirectoryCreator
import com.gilt.lucene.WritableLuceneIndex
import com.hp.hpl.jena.rdf.model.NodeIterator
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.rdf.model.ResIterator
import com.hp.hpl.jena.rdf.model.Resource
import org.apache.lucene.document.Document
import com.gilt.lucene.LuceneFieldHelpers._
import com.gilt.lucene.LuceneText._
import org.scalatest.words.JavaCollectionWrapper
import com.gilt.lucene.LuceneDocumentAdder.LuceneDocumentLike
import java.io.FileFilter

trait SpecifiableLuceneIndexPathProvider extends LuceneIndexPathProvider {
    val path : File
    override def withIndexPath[T](f: (File) => T): T = {  
       path.mkdirs()
       f(path)
    }
}

object SurfaceFormIndexer {
  
  def indexExists(into : File) = {
    val files = into.listFiles(new FileFilter(){
      override def accept(pathname : File) = {
        pathname.getName equals "segments.gen" 
      }
    })
    files.length == 1
  }
}

/**
 * @author dhaeb
 */
class SurfaceFormIndexer(indexable : File, into : File) {
  import SurfaceFormIndexer._
  
  require(indexable.exists() && indexable.isFile())
  require(into.isDirectory())
  
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
  
  def parseFile() = {
    val model = RDFDataMgr.loadModel(indexable.getAbsolutePath)
    val stream : Stream[Resource] = ResourceStream(model.listSubjects())()
    val dcat = "http://www.w3.org/2004/02/skos/core#altLabel";
    val p : Property  = model.createProperty(dcat);
    stream.toSeq.par.map  { subject => 
    val listObjectsOfProperty : NodeIterator = model.listObjectsOfProperty(subject, p);
      def preprocessString(s : Any) = s.toString().toLowerCase()
      val returnable = new Document()
      returnable.addStoredOnlyField( "uri", subject.getURI)
      returnable.addIndexedStoredField("uriName", preprocessString(subject.getLocalName))
      for(obj <- listObjectsOfProperty){
    	  returnable.addIndexedStoredField("label", preprocessString(obj))
      }
      returnable
    }.seq
  }
  
  val queryParser = indexManager.queryParserForDefaultField("label")
  
  def query(queryString : String) = {
    val query = queryParser.parse(queryString)
    indexManager.searchTopDocuments(query, 5)
  }
  
}