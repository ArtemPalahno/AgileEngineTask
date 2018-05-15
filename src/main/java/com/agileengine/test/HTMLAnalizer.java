package com.agileengine.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Artem_Palahno on 5/15/2018.
 */
public class HTMLAnalizer {
    private static final String ELEMENT_ID = "make-everything-ok-button";
    private static final Logger logger = LogManager.getLogger(HTMLAnalizer.class);
    private static final String EMPTY_STRING = "";
    private static final String ROOT_ELEMENT_NAME = "#root";

    private final Deque<String> tags;

    public HTMLAnalizer() {
        tags = new LinkedList<>();
    }

    public static void main(String[] args) throws IOException {
        final HTMLAnalizer htmlAnalizer = new HTMLAnalizer();
        htmlAnalizer.preparePath(args[0]);
        Element found = htmlAnalizer.found(args[1]);

        System.out.println(found);
    }

    private void preparePath(String path) {
        Optional<Element> element = foundElementById(path);
        element.ifPresent(element1 -> {
            Element par = element1.parent();
            while (par != null && !Objects.equals(par.tagName(), ROOT_ELEMENT_NAME)) {
                tags.add(par.className());
                par = par.parent();
            }
        });

    }

    private Optional<Element> foundElementById(String path) {
        Optional<Document> doc = Optional.ofNullable(Jsoup.parse(readFileToString(path)));
        return doc.flatMap(document -> Optional.ofNullable(document.getElementById(ELEMENT_ID)));
    }

    private static String readFileToString(String fileName) {
        String string = EMPTY_STRING;
        try {
            string = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            logger.error(e);
        }
        return string;
    }

    public Element found(String path) {
        Document doc = Jsoup.parse(readFileToString(path));
        Element element = null;
        String className;
        while (element == null) {
            className = tags.pollLast();
            if (!Objects.equals(className, EMPTY_STRING)) {
                element = doc.getElementsByClass(className).last();
            }
        }
        return element;
    }

}
