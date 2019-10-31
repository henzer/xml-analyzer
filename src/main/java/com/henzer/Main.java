package com.henzer;

import info.debatty.java.stringsimilarity.Levenshtein;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.Entry.comparingByValue;

public class Main {
    private static final String TARGET = "make-everything-ok-button";

    public static void main(final String[] args) throws IOException {
        if (args.length == 2) {
            final String originalFilePath = args[0];
            final String sampleFilePath = args[1];
            final Document originalFile = getHtmlDocumentFromFileName(originalFilePath);
            final Document sampleFile = getHtmlDocumentFromFileName(sampleFilePath);
            final String targetElement = getTargetElementById(originalFile, TARGET);
            final Map<String, Double> result = createSimilarityMap(targetElement, sampleFile);
            final String cssSelector = getMoreSimilar(result);

            System.out.println("------------------------- RESULTS ----------------------------");
            System.out.println("CSS Selector: " + cssSelector);
            System.out.println("Element: " + sampleFile.select(cssSelector));
        } else {
            throw new IllegalArgumentException("It was expecting two arguments");
        }
    }

    private static String getTargetElementById(final Document document, final String id) {
        return document.getElementById(id).toString();
    }

    private static Document getHtmlDocumentFromFileName(final String fileName) throws IOException {
        final File file = new File(fileName);
        return Jsoup.parse(file, "UTF-8", "");
    }

    private static Map<String, Double> createSimilarityMap(final String target, final Document document) {
        //We can implement whatever algorithm we want to calculate similarity. I decided to use Levenshtein
        final Levenshtein l = new Levenshtein();
        final Elements elements = document.body().getAllElements();
        final Map<String, Double> result = new HashMap();
        for (Element element : elements) {
            if (element.children().size() == 0) {
                result.put(element.cssSelector(), l.distance(target, element.toString()));
            }
        }
        return result;
    }

    private static String getMoreSimilar(final Map<String, Double> map) {
        return map
                .entrySet()
                .stream()
                .min(comparingByValue())
                .get()
                .getKey();
    }
}
