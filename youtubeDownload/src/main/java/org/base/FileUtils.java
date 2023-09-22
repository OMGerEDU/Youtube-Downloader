package org.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FileUtils {

    //run the script using cmd executable and return the exit code
    public static Object runScript(String cmd, String path) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("cmd");
        commands.add("/c");
        commands.add(cmd);
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File(path));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String output = flushInputStreamReader(process, false);
        System.out.println(output);
        int exitCode = process.waitFor();
        TimeUnit.SECONDS.sleep(1);
        return exitCode == 0;
    }

    //helper to read the output of the script using BufferReader and return the output, onlyLastLine is optional in case you don't want the full output.
    private static String flushInputStreamReader(Process process, Boolean... onlyLastLine) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String lastLine = "";
        StringBuilder s = new StringBuilder();

        while ((line = input.readLine()) != null) {
            s.append(line);
            lastLine = line;
        }
        boolean lastLineFlag = (onlyLastLine.length > 0) ? onlyLastLine[0] : false;
        return lastLineFlag ? lastLine : s.toString();
    }

}
