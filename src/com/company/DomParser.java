package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class DomParser {

    public static void main(String[] args) throws InterruptedException {
        XmlReader xmlReader = new XmlReader();
        PotokReader potokReader = new PotokReader(xmlReader);
        PotokWriter potokWriter = new PotokWriter(potokReader);

        xmlReader.start();
        xmlReader.join();
        potokReader.start();
        potokReader.join();
        potokWriter.start();
    }

    public static class XmlReader extends Thread {
        private int kolvoId;

        private ArrayList<Integer> threadNumberCollection = new ArrayList<>();
        private ArrayList<Integer> primeNumberGeneratedCollection = new ArrayList<>();

        public ArrayList<Integer> getThreadNumberCollection() {
            return threadNumberCollection;
        }

        public ArrayList<Integer> getPrimeNumberGeneratedCollection() {
            return primeNumberGeneratedCollection;
        }

        public void myKolvoId(int kolvoId) {
            this.kolvoId = kolvoId;
        }

        public int getKolvoId() {
            return kolvoId;
        }

        @Override
        public void run() {
            try {
                File testTask = new File("TestTask.xml");
                DocumentBuilderFactory ttFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder ttBuilder = ttFactory.newDocumentBuilder();
                Document doc = ttBuilder.parse(testTask);
                doc.getDocumentElement().normalize();
                NodeList idList = doc.getElementsByTagName("interval");

                for (int i = 0; i < idList.getLength(); i++) {
                    Node idNode = idList.item(i);

                    myKolvoId(idList.getLength());

                    if (idNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) idNode;

                        int min = Integer.parseInt(eElement.getElementsByTagName("low").item(0).getTextContent());
                        int max = Integer.parseInt(eElement.getElementsByTagName("high").item(0).getTextContent());

                        threadNumberCollection.add(Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent()));
                        primeNumberGeneratedCollection.add(random(min, max));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        private static int random(int min, int max) {
            int delta = max - min;
            return (int) (Math.random() * ++delta) + min;
        }
    }

    public static class PotokReader extends Thread {
        private XmlReader xmlRead;

        public PotokReader(XmlReader xmlRead) {
            this.xmlRead = xmlRead;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < xmlRead.getKolvoId(); i++) {
                    System.out.println("\nThread Number " + xmlRead.getThreadNumberCollection().get(i) +
                            "\nprime number generated: " + xmlRead.getPrimeNumberGeneratedCollection().get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class PotokWriter extends Thread {
        private PotokReader potokRead;

        public PotokWriter(PotokReader potokRead) {
            this.potokRead = potokRead;
        }

        @Override
        public void run() {
            try(FileWriter fileWriter = new FileWriter("File.txt")) {
                for (int i = 0; i < potokRead.xmlRead.getKolvoId(); i++) {
                    fileWriter.write(String.valueOf("\n\nThread Number " + potokRead.xmlRead.getThreadNumberCollection().get(i) +
                            "\nprime number generated: " + potokRead.xmlRead.getPrimeNumberGeneratedCollection().get(i)));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}