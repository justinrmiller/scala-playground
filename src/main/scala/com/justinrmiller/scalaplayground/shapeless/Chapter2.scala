package com.justinrmiller.scalaplayground.shapeless

import shapeless.{::, HNil}

/**
  * Created by justin on 4/30/17.
  *
  * Chapter 2 examples from The Type Astronaut's Guide to Shapeless
  */
object Chapter2 {
  sealed trait Shape
  final case class Rectangle(width: Double, height: Double) extends Shape
  final case class Circle(radius: Double) extends Shape

  val rect: Shape = Rectangle(3.0, 4.0)
  val circ: Shape = Circle(1.0)

  def area(shape: Shape) = {
    shape match {
      case Rectangle(w, h) => w * h
      case Circle(r) => math.Pi * r * r
    }
  }

  type Rectangle2 = (Double, Double)
  type Circle2 = Double
  type Shape2 = Either[Rectangle2, Circle2]

  val rect2: Shape2 = Left((3.0, 4.0))
  val circ2: Shape2 = Right(1.0)

  def area2(shape: Shape2) = shape match {
    case Left((w, h)) => w * h
    case Right(r) => math.Pi * r * r
  }

  def main(args: Array[String]) {
    println(area(rect))
    println(area2(rect2))
    println(area2(circ2))

    import shapeless.{HList, ::, HNil}

    val product: String :: Int :: Boolean :: HNil =
      "Sunday" :: 1 :: false :: HNil

    println(product.head)
    println(product.tail.head)
    println(product.tail.tail)

    import shapeless.Generic

    case class IceCream(name: String, numCherries: Int, inCone: Boolean)

    val iceCreamGen = Generic[IceCream]

    val iceCream = IceCream("Sundae", 1, false)
    println(iceCream)
    val repr = iceCreamGen.to(iceCream)
    println(repr)
    println(iceCreamGen.from(repr))

    val tupleGen = Generic[(String, Int, Boolean)]

    println(tupleGen.to(("Hello", 123, true)))

    println(tupleGen.from("Hello" :: 123 :: true :: HNil))
  }
}
