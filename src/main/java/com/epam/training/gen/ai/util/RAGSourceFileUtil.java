package com.epam.training.gen.ai.util;

import com.github.jknack.handlebars.internal.lang3.StringUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RAGSourceFileUtil {

    public static List<String> splitFileIntoSentences(File inputFile) throws IOException {
        List<String> sentences = new ArrayList<>();

        try (InputStream inputStream = new FileInputStream(inputFile)) {
            String text = IOUtils.toString(inputStream, String.valueOf(StandardCharsets.UTF_8));
            // Using a very basic sentence delimiter split, which considers .!? as end of sentences.
            String[] sentenceArray = text.split("[.!?]\\s*");
            for (String sentence : sentenceArray) {
                if (StringUtils.isNotBlank(sentence)) {
                    sentences.add(sentence.trim());
                }
            }
        }

        return sentences;
    }

    public static List<String> splitFileByCharsWithOverlap(File inputFile, int chunkSize, int overlap) throws IOException {
        List<String> chunks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();

            // Read the entire file into a single StringBuilder
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                stringBuilder.append(buffer, 0, read);
            }

            String content = stringBuilder.toString();
            int index = 0;  // Start index of each chunk

            while (index < content.length()) {
                // Calculate the end index for the substring method
                int endIndex = Math.min(index + chunkSize, content.length());

                // Extract the chunk
                chunks.add(content.substring(index, endIndex));

                // Move the index forward by chunkSize minus overlap
                if (endIndex == content.length()) break; // End of file condition
                index += (chunkSize - overlap);
            }
        }

        return chunks;
    }

}
