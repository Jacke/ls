resolvers ++= Seq(
//  "lessis" at "http://repo.lessis.me",
  "coda" at "http://repo.codahale.com"
  //,Resolver.url("sbt-plugin-releases", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
)

//addSbtPlugin("me.lessis" % "ls-sbt" % "0.1.1")

//addSbtPlugin("net.databinder" % "conscript-plugin" % "0.3.3")

libraryDependencies <+= sbtVersion("org.scala-sbt" % "scripted-plugin" % _)

addSbtPlugin("com.jsuereth" % "xsbt-gpg-plugin" % "0.6")
