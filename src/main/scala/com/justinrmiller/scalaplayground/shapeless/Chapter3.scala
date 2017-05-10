package com.justinrmiller.scalaplayground.shapeless

import com.justinrmiller.scalaplayground.shapeless.Chapter3.CsvEncoder
import shapeless.{::, Generic, HList, HNil}

/**
  * Created by justin on 4/30/17.
  *
  * Chapter 3 examples from The Type Astronaut's Guide to Shapeless
  */
object CsvEncoder {
  // "Summoner" method
  def apply[A](implicit enc: CsvEncoder[A]): CsvEncoder[A] =
    enc

  // "Constructor" method
  def createEncoder[A](func: A => List[String]): CsvEncoder[A] =
    new CsvEncoder[A] {
      def encode(value: A): List[String] =
        func(value)
    }

  implicit val stringEncoder: CsvEncoder[String] =
    createEncoder(str => List(str))
  implicit val intEncoder: CsvEncoder[Int] =
    createEncoder(num => List(num.toString))
  implicit val booleanEncoder: CsvEncoder[Boolean] =
    createEncoder(bool => List(if(bool) "yes" else "no"))
}

object Chapter3 {
  import CsvEncoder._

  trait CsvEncoder[A] {
    def encode(value: A): List[String]
  }

  case class Employee(name: String, number: Int, manager: Boolean)
  case class IceCream(name: String, numCherries: Int, inCone: Boolean)

  implicit val employeeEncoder: CsvEncoder[Employee] =
    new CsvEncoder[Employee] {
      def encode(e: Employee): List[String] = List(
        e.name,
        e.number.toString,
        if (e.manager) "Yes" else "No"
      )
    }

  implicit val iceCreamEncoder: CsvEncoder[IceCream] = {
    val gen = Generic[IceCream]
    val enc = CsvEncoder[gen.Repr]
    createEncoder(iceCream => enc.encode(gen.to(iceCream)))
  }
  implicit def genericEncoder[A, R](
    implicit
    gen: Generic[A] { type Repr = R },
    enc: CsvEncoder[R]
  ): CsvEncoder[A] =
    createEncoder(a => enc.encode(gen.to(a)))

  implicit def pairEncoder[A, B](
    implicit aEncoder: CsvEncoder[A], bEncoder: CsvEncoder[B]
  ): CsvEncoder[(A, B)] =
    new CsvEncoder[(A, B)] {
      override def encode(pair: (A, B)): List[String] = {
        val (a, b) = pair
        aEncoder.encode(a) ++ bEncoder.encode(b)
      }
    }

  def writeCsv[A](values: List[A])(implicit enc: CsvEncoder[A]): String =
    values.map(value => enc.encode(value).mkString(",")).mkString("\n")


  implicit val hnilEncoder: CsvEncoder[HNil] =
    createEncoder(hnil => Nil)
  implicit def hlistEncoder[H, T <: HList](
    implicit
    hEncoder: CsvEncoder[H],
    tEncoder: CsvEncoder[T]
  ): CsvEncoder[H :: T] =
    createEncoder {
      case h :: t =>
        hEncoder.encode(h) ++ tEncoder.encode(t)
    }

  def main(args: Array[String]) {
    val employees: List[Employee] = List(
      Employee("Bill", 1, true),
      Employee("Peter", 2, false),
      Employee("Milton", 3, false)
    )

    println(writeCsv(employees))

    val iceCreams: List[IceCream] = List(
      IceCream("Sundae", 2, false),
      IceCream("Cornetto", 0, true)
    )

    println("\n" + writeCsv(iceCreams))

    println("\n" + writeCsv(employees zip iceCreams))

    val iceCreamEncoder = CsvEncoder[IceCream]

    val reprEncoder: CsvEncoder[String :: Int :: Boolean :: HNil] =
      implicitly

    println(reprEncoder.encode("abc" :: 123 :: true :: HNil))
  }
}
