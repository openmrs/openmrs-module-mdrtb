package org.openmrs.module.mdrtb.web.patientsummary.display;

import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.HtmlSnippets;
import org.jmesa.view.html.HtmlView;


public class CustomTableHtmlView extends HtmlView {

	public Object render() {
		
	        HtmlSnippets snippets = getHtmlSnippets();

	        HtmlBuilder html = new HtmlBuilder();

	        html.append(snippets.themeStart());

	        html.append(snippets.tableStart());

	        html.append(snippets.theadStart());

	       // html.append(snippets.toolbar());

	      // html.append(snippets.filter());

	        // added my own sub-header here
	        html.append(customHeader());
	        
	        html.append(snippets.header());

	        html.append(snippets.theadEnd());

	        html.append(snippets.tbodyStart());

	        html.append(snippets.body());

	        html.append(snippets.tbodyEnd());

	        html.append(snippets.footer());

	       // html.append(snippets.statusBar());

	        html.append(snippets.tableEnd());

	        html.append(snippets.themeEnd());

	       // html.append(snippets.initJavascriptLimit());

	        return html.toString();
    }
	
	private String customHeader() {
        HtmlBuilder html = new HtmlBuilder();
        html.tr(1).styleClass("patientSummaryTableHeader").close();
        html.td(2).colspan("1").close().append("&#160;").tdEnd();
        html.td(2).colspan("2").close().append("Bacterologies").tdEnd(); // TODO: need to make these come from message service so that they are localized!!
        html.td(2).colspan("16").close().append("DSTs").tdEnd(); 
        html.trEnd(1);
        return html.toString();
    }

}
