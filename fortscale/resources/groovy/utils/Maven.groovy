def executeMavenCommand(String command, String javaHome, String mavenHome) {
    process = command.execute(["JAVA_HOME=${javaHome}/.."], new File("${mavenHome}/bin"))
    output = new StringBuffer()
    error = new StringBuffer()
    process.consumeProcessOutput(output, error)
    process.waitFor()
    System.out.println(output)
    System.err.println(error)
    exitValue = process.exitValue()
    exitMessage = "Exit value: ${exitValue}."

    if (exitValue == 0) {
        System.out.println(exitMessage)
    } else {
        System.err.println(exitMessage)
        throw new RuntimeException()
    }
}
