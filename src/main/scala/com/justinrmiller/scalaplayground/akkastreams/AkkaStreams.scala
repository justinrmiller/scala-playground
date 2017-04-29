package com.justinrmiller.scalaplayground.akkastreams

import java.nio.file.Paths

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.Future

object AkkaStreams {
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  def print1to100() {
    val source: Source[Int, NotUsed] = Source(1 to 100)

    source.runForeach(i => println(i))(materializer)
  }

  def factorials() {
    val source: Source[Int, NotUsed] = Source(1 to 100)

    val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

    val result: Future[Done] =
      factorials
        //.runWith(FileIO.toPath(Paths.get("/Users/justinmiller/factorials.txt")))
        .runForeach(i => println(i))(materializer)
  }

  def main(args: Array[String]): Unit = {
    factorials()
  }
}
