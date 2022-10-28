//trick for jdk compiler
module jam.compiler {
    //https://docs.oracle.com/en/java/javase/11/docs/api/jdk.compiler/module-summary.html
//    requires jdk.compiler;
    requires java.compiler;
    requires jam.common;
}