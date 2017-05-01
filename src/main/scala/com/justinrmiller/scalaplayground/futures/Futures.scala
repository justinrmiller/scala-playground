package com.justinrmiller.scalaplayground.futures

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Examples taken from a walkthrough of new Scala Future features in 2.12 by the awesome Victor Klang
  * See blog post here: http://viktorklang.com/blog/Futures-in-Scala-2.12-part-1.html
  */
object Futures {
  def flatten() {
    println("Constructing a future of future of a string Bob")
    val future = Future(Future("Bob"))

    println("Applying flatten to future of future of String Bob")
    val flattenedFuture: Future[String] = future.flatten

    println(s"Value of future: $future")
    println(s"Value of flattened future: $flattenedFuture")
  }

  def combinedFuture() {
    val future1 = Future("Bob")
    val future2 = Future(24)

    val combinedFuture: Future[String] = future1.zipWith(future2)((name, age) => s"$name is $age years old")
    val sentence = Await.result(combinedFuture, 5.seconds)
    println(sentence)
  }

  def unitFuture() {
    def store(s: String) = Future.unit // <-- business logic here

    def storeInDB(s: String): Future[Unit] = s match {
      case null | "" => Future.unit
      case other =>
        store(s)
    }

    val f: Future[String]  = Future("Bob")
    val f2 = f flatMap storeInDB

    println(s"Storing Bob: ${Await.result(f2, 5.seconds)}")
  }

  def main(args: Array[String]) {
    println("Flatted futures:")
    flatten()

    println("\nCombined future (zipWith):")
    combinedFuture()

    println("\nUnit future:")
    unitFuture()
  }
}
