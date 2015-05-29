package de.aksw.library

import org.junit.runner._
import org.junit.Test
import org.apache.lucene.document.Document
import com.gilt.lucene._
import com.gilt.lucene.LuceneFieldHelpers._
import org.scalatest.junit.AssertionsForJUnit
import org.apache.lucene.search.TermQuery
import org.apache.lucene.index.Term


/**
 * @author dhaeb
 */
class LuceneSugarLibraryTest extends AssertionsForJUnit {

  @Test
  def testWriteIndex = {
    val index = new ReadableLuceneIndex 
                with WritableLuceneIndex 
                with LuceneStandardAnalyzer 
                with RamLuceneDirectory
    val doc = new Document
    val field = "aField"
    val defaultValue = "aValue"
    
    def createFixture(value: String) = {
      doc.addIndexedStoredField(field, value)
      index.addDocument(doc)
    }
    
    createFixture(defaultValue)
    createFixture(defaultValue.toLowerCase())
    index.allDocuments.foreach(println(_))
    val t = new Term(field, defaultValue)
    val tq = new TermQuery(t)
    var results = index.searchTopDocuments(tq, 2)
    assert(2 === results.size)
    
    val queryParser = index.queryParserForDefaultField(field)
    val query = queryParser.parse(defaultValue) // analyser will convert it to lower case
    results = index.searchTopDocuments(query, 2)
    assert(1 === results.size)
  }
}