/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package penny.downloadmanager.control.processor;

import java.net.URISyntaxException;
import penny.downloadmanager.model.Model;
import penny.downloadmanager.model.db.Download;
import penny.downloadmanager.model.gui.ParsingModel;
import penny.parser.LinkEater;
import penny.parser.LinkExtractor;
import penny.parser.WordEater;
import penny.parser.WordExtractor;

/**
 *
 * @author john
 */
public class Parser implements LinkEater, WordEater {

    private LinkExtractor linkExtractor;
    private WordExtractor wordExtractor;
    private ParsingModel parsingModel;
    private Download download;

    public Parser(Download download) throws URISyntaxException {
        this.download = download;
        parsingModel = Model.getApplicationSettings().getParsingModel();
        wordExtractor = new WordExtractor(this);
        linkExtractor = new LinkExtractor(download.getUrl().toURI(), this);
        linkExtractor.setLinkState(download.getLinkState());
        wordExtractor.setWordBuffer(download.getWordBuffer());
    }
    
    public void parse(int read, byte[] buffer) {
        if (Model.parseLinks(download)) {
            linkExtractor.put(buffer, 0, read);
            download.setLinkState(linkExtractor.getLinkState());
        }
        if (Model.parseWords(download)) {
            wordExtractor.put(buffer, 0, read);
            download.setWordBuffer(wordExtractor.getWordBuffer());
        }
    }
    
    public void reset() throws URISyntaxException {
        wordExtractor = new WordExtractor(this);
        linkExtractor = new LinkExtractor(download.getUrl().toURI(), this);
        download.setWordBuffer("");
        download.setLinkState(linkExtractor.getLinkState());
    }

    @Override
    public void eatLink(String url, boolean src) {
        if (src) {
            download.addSrcLink(url);
        } else {
            download.addHrefLink(url);
        }
    }

    @Override
    public void eatWord(String word) {
        download.addWord(word);
    }
}
