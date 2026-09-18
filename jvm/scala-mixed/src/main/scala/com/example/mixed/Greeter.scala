package com.example.mixed

/** The Scala half: builds the line a Java caller shows. Java reaches it as an ordinary class. */
class Greeter(salutation: String):
  def greet(name: String): String = s"$salutation, $name!"

  def greetAll(names: Seq[String]): String = names.map(greet).mkString("\n")
