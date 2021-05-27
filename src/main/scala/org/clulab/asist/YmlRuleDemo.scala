/**
 * Authors:  Joseph Astier, Adarsh Pyarelal
 *
 * Updated:  2021 May
 *
 * Read .yml or .yaml rules and generate .md output
 *
 * Snake YAML implementation based on: 
 *    https://www.baeldung.com/java-snake-yaml
 *
 * Additional documentation from:
 *    https://bitbucket.org/asomov/snakeyaml/wiki/Documentation
 *
 * @param inputFilename A file or directory of files to process.
 * @param outputFilename The results of all file processing are written here
 */
package org.clulab.asist

//import shapeless.syntax.typeable._
import java.io.File
import java.io.InputStream
import java.io.FileInputStream
import java.io.PrintWriter

import org.clulab.asist.AsistEngine._

import java.util.Collection
import java.util.LinkedHashMap
import java.util.ArrayList
import org.slf4j.LoggerFactory
import scala.io.Source
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.clulab.utils.{Configured, FileUtils}

import scala.collection.JavaConverters._

import org.clulab.odin.impl._
import org.clulab.odin.impl.RuleReader._


// for now 

class Rule {
  val name: String = ""
  val label: String = ""
}

// for now 
 /*
class Rule {
  val _import: String = ""
  val vars: LinkedHashMap[String, String] = new LinkedHashMap
}
*/


class YmlRuleDemo(
  override val inputFilename: String = "",
  override val outputFilename: String = ""
) extends RuleDemo (inputFilename, outputFilename) {

  private lazy val logger = LoggerFactory.getLogger(this.getClass())

  override def process(
    filename: String,
    str: String,
    output: PrintWriter
  ): Unit = {
    logger.info("Processing %s\n".format(filename))

    val yaml = new Yaml
    val javaMap = yaml
      .load(str)
      .asInstanceOf[java.util.LinkedHashMap[String, ArrayList[Any]]]

    val map = javaMap.asScala
    val keys = map.keySet
    logger.info("number of keys found: %d".format(keys.size))

    val keyIter = keys.iterator

    while(keyIter.hasNext) {
      val keyStr = "KEY: %s\n".format(keyIter.next)
      System.out.println(keyStr)
      output.write(keyStr)
    }

    showRules(map.getOrElse("rules", new ArrayList[Any]), output)

  }

  def showRules(rules: ArrayList[Any], output: PrintWriter): Unit = {
    output.write("RULES:\n")
    output.write("number of rules found: %d\n".format(rules.size))
    val ruleIter = rules.iterator
    while(ruleIter.hasNext) showRule(ruleIter.next.toString, output)
  }


  def showRule(rule: String, output: PrintWriter): Unit = {
    output.write("RULE:\n")
    output.write("%s\n".format(rule.toString))
    output.write("\n")
    /*
    val map: LinkedHashMap[String, ArrayList[Any]] = 
      yaml.load(rule).asInstanceOf[LinkedHashMap[String, ArrayList[Any]]]
    val keys = map.keySet
    System.out.println("number of keys found: %d".format(keys.size))

    val keyIter = keys.iterator

    while(keyIter.hasNext) {
      System.out.println("RULE KEY: %s".format(keyIter.next))
    }
    */
  }

  this(inputFilename, outputFilename)
}