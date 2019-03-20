import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;

class TwoWayDictionary {
    private TreeMap<String, TreeSet<String>> _englishToVietnamese;
    private TreeMap<String, TreeSet<String>> _vietnameseToEnglish;

    TwoWayDictionary() {
        _englishToVietnamese = new TreeMap<>();
        _vietnameseToEnglish = new TreeMap<>();
    }

    private void add(String english, TreeSet<String> vietnameses) {
        TreeSet<String> temp = _englishToVietnamese.put(english, new TreeSet<>(vietnameses));
        if (temp != null) {
            _englishToVietnamese.get(english).addAll(temp);
        }

        for (String vietnamese : vietnameses) {
            temp = _vietnameseToEnglish.put(vietnamese, new TreeSet<>());
            _vietnameseToEnglish.get(vietnamese).add(english);
            if (temp != null) {
                _vietnameseToEnglish.get(vietnamese).addAll(temp);
            }
        }
    }

    void loadData(String dataFilePath) throws ParserConfigurationException, IOException, SAXException {
        File f = new File(dataFilePath);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder buider = factory.newDocumentBuilder();
        Document doc = buider.parse(f);
        Element dictionary = doc.getDocumentElement();
        NodeList wordList = dictionary.getElementsByTagName("word");

        for (int i = 0; i < wordList.getLength(); i++) {
            Node wordNode = wordList.item(i);
            if (wordNode.getNodeType() == Node.ELEMENT_NODE) {
                Element word = (Element) wordNode;
                String english = word.getElementsByTagName("english").item(0).getTextContent();
                NodeList vietnameseNodeList = word.getElementsByTagName("vietnamese");
                TreeSet<String> vietnameses = new TreeSet<>();

                for (int j = 0; j < vietnameseNodeList.getLength(); j++) {
                    Node vietnameseNode = vietnameseNodeList.item(j);
                    if (vietnameseNode.getNodeType() == Node.ELEMENT_NODE) {
                        String vietnamese = ((Element) vietnameseNode).getTextContent();
                        vietnameses.add(vietnamese);
                    }
                }

                this.add(english, vietnameses);
            }
        }
    }

    TreeSet<String> getEnglishMeanings(String vietnamese) {
        return _vietnameseToEnglish.get(vietnamese);
    }

    TreeSet<String> getVietnameseMeanings(String english) {
        return _englishToVietnamese.get(english);
    }
}