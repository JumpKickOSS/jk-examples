package com.example.mixed

/** Entry point in Scala, reading its default from the Java half: the two halves reference each other. */
object Main:
  def main(args: Array[String]): Unit =
    val names = if args.isEmpty then Seq(Greeting.defaultName()) else args.toSeq
    println(Greeter(Greeting.SALUTATION).greetAll(names))
