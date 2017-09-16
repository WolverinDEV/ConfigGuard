package dev.wolveringer.config.yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wolverindev on 09.09.17.
 */
public class YamlCommentHelper {
    public static String join(List<String> list, String conjunction) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first) {
                first = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }

        return sb.toString();
    }

    public static String addComments(String yamlString, HashMap<String, List<String>> comments){
        Integer depth = 0;
        ArrayList<String> keyChain = new ArrayList<>();

        StringBuilder writeLines = new StringBuilder();
        comments.getOrDefault("", new ArrayList<>()).forEach(e -> writeLines.append("#" + e + "\n"));
        for (final String originalLine : yamlString.split("\n")) {
            String line = originalLine;
            int index = 0;
            while(line.length() >= index){
                if(line.charAt(index) == '-'){
                    line = line.replaceFirst("-", " ");
                } else if(line.charAt(index) != ' ') break;
                index++;
            }

            if (line.startsWith(new String(new char[depth]).replace("\0", " "))) {
                keyChain.add(line.split(":")[0].trim());
                depth = depth + 2;
            } else {
                if (line.startsWith(new String(new char[depth - 2]).replace("\0", " "))) {
                    keyChain.remove(keyChain.size() - 1);
                } else {
                    //Check how much spaces are infront of the line
                    int spaces = 0;
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ' ') {
                            spaces++;
                        } else {
                            break;
                        }
                    }

                    depth = spaces;

                    if (spaces == 0) {
                        keyChain = new ArrayList<>();
                        depth = 2;
                    } else {
                        ArrayList<String> temp = new ArrayList<>();
                        index = 0;
                        for (int i = 0; i < spaces; i = i + 2, index++) {
                            temp.add(keyChain.get(index));
                        }

                        keyChain = temp;

                        depth = depth + 2;
                    }
                }

                keyChain.add(line.split(":")[0].trim());
            }

            String search;
            if (keyChain.size() > 0) {
                search = join(keyChain, ".");
            } else {
                search = "";
            }

            System.out.println("Searching for " + search);
            if (comments.containsKey(search)) {
                for (String comment : comments.get(search)) {
                    writeLines.append(new String(new char[depth - 2]).replace("\0", " "));
                    writeLines.append("# ");
                    writeLines.append(comment);
                    writeLines.append("\n");
                }
            }

            writeLines.append(originalLine);
            writeLines.append("\n");
        }

        return writeLines.toString();
    }
}
