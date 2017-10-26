package cey.training.personal.android_iot.smarthome;

import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import cey.training.personal.android_iot.R;

/**
 * Created by ceyler on 23.12.2016.
 *
 */

public class ReportPDF {//TODO redo in other thread
    private int MAX_LINE_AMOUNT = 14;

    private PdfDocument report;
    private int pageCounter;
    private int lineCounter;
    private StringBuilder dataToWrite;

    private SmartHome parent;
    private FileOutputStream out;

    public ReportPDF(SmartHome parent){
        this.parent = parent;

        report = new PdfDocument();
        try {
            File reportOut = new File(parent.getActivity().getExternalFilesDir(null), "report.pdf");
            log("Report at " + reportOut.getAbsolutePath());
            out = new FileOutputStream(reportOut);
        } catch (FileNotFoundException ex){
            log(ex.getLocalizedMessage());
        }
        pageCounter = lineCounter = 1;
        dataToWrite = new StringBuilder();

        log("report opened");
    }

    public void addToReport(String data){
        //TODO write current time and date to pdf
        dataToWrite.append(data);
        dataToWrite.append("\n");

        log("Page " + Integer.toString(pageCounter) +
                " line " + Integer.toString(lineCounter) +
                ": " + data);

        if(lineCounter >= MAX_LINE_AMOUNT){
            writePage();
            lineCounter = 0;
        }

        ++lineCounter;
    }

    private void writePage(){
        PageInfo pageInfo = new PageInfo.Builder(720, 1280, pageCounter).create();
        Page dataPage = report.startPage(pageInfo);

        LayoutInflater inflater = parent.getActivity().getLayoutInflater();

        TextView content = (TextView) inflater.inflate(R.layout.pdf_view, null, false);
        content.measure(1280, 720);
        content.layout(0, 0, 1280, 720);
        content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        content.setText(dataToWrite.toString());

        content.draw(dataPage.getCanvas());

        report.finishPage(dataPage);
        ++pageCounter;

        log("page finished");

        dataToWrite.delete(0, dataToWrite.length());
    }

    public void finishReport(){
        if(dataToWrite.length() > 0) {
            writePage();
        }

        if(out != null) {
            try {
                report.writeTo(out);
                out.close();
            } catch (IOException ex) {
                log(ex.getLocalizedMessage());
            }
        }

        report.close();
        log("report closed");
    }

    private void log(String msg){
        parent.log("PDF " + msg);
    }
}
