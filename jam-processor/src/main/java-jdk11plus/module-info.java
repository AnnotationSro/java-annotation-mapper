//trick for jdk compiler
module jdk.jshell {
    //https://docs.oracle.com/en/java/javase/11/docs/api/jdk.compiler/module-summary.html
    requires jdk.compiler;
    requires jam.common;
}