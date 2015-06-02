package de.aksw.iterator

import com.hp.hpl.jena.rdf.model.{Resource, ResIterator}
import com.hp.hpl.jena.util.iterator.ExtendedIterator

/**
 * Created by dhaeb on 02.06.15.
 */
object ExtendedIteratorStream {

  def apply[T](it : ExtendedIterator[T])() = {
    if(it.hasNext)
      new ExtendedIteratorStream[T](it)
    else
      Stream.empty[T]
  }
}

class ExtendedIteratorStream[T](it : ExtendedIterator[T]) extends Stream[T] {
  val res : T = it.next()
  override def isEmpty = false
  override def head = res
  override def tail = ExtendedIteratorStream.apply(it)()
  def tailDefined = true
}
