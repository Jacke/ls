package ls

import java.io.{ File, FileWriter }
import dispatch._, dispatch.Defaults._

object LsInit {

  def main(args: Array[String]) {
    val exit = run(args)
    System.exit(exit)
  }

  def run(args: Array[String]): Int =
    args match {
      case Array("--version") =>
        println("0.1.3")
        0
      case _ =>
        val base = new File(args.headOption.getOrElse("."))
        val result = if (base.isDirectory) {
          DefaultClient { _.Handler.latest("ls-sbt") }
        } else {
          Left("Directory not found: " + base.getCanonicalPath)
        }
        result.fold({ err => println(err); 1 }, { resp =>
          resp.map(_.fold({ err =>
            println("unexpected http error %s" format err)
            0
          }, { version =>
            setup(base, version)
            1
          })).apply()
       })
    }

  def setup(base: File, version: String) = {
    val build = new File(base, "build.sbt")
    val plugin = new File(base, "project/plugins.sbt")
    val files = build :: plugin :: Nil
    def stat(f: File) =
      "%s: %s".format(
        if (f.exists) "Updated" else "Created",
        f.getCanonicalPath
      )
    val msg = files.map(stat).mkString("\n")
    files.foreach { f =>
      new File(f.getParent).mkdirs()
    }
    def write(f: File)(block: FileWriter => Unit) {
      val writer = new FileWriter(f, true)
      try {
        block(writer)
      } finally {
        writer.close()
      }
    }
    write(build) { fw =>
      fw.write("""
      |
      |seq(lsSettings :_*)
      |""".stripMargin)
    }
    write(plugin) { fw =>
      fw.write("""
      |
      |addSbtPlugin("me.lessis" % "ls-sbt" % "%VERSION%")
      |""".replace("%VERSION%", version).stripMargin)
    }
    Right(msg)
  }
}
class LsInit extends xsbti.AppMain {
  def run(config: xsbti.AppConfiguration) =
    new Exit(LsInit.run(config.arguments))
}
class Exit(val code: Int) extends xsbti.Exit
