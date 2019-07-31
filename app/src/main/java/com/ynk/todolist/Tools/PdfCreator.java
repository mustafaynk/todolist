package com.ynk.todolist.Tools;

import android.content.Context;
import android.os.Environment;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ynk.todolist.Database.AppDatabase;
import com.ynk.todolist.Database.DAO;
import com.ynk.todolist.Model.TodoList;
import com.ynk.todolist.Model.TodoListItem;
import com.ynk.todolist.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class PdfCreator {

    private Document document;
    private String documentPath, path;
    private SimpleDateFormat sdf;
    private Context context;
    private Font fontHeader12;

    private DAO dao;

    public PdfCreator(Context actContext) {
        sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("tr"));
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getPath() + File.separator + "todolist";
        dao = AppDatabase.getDb(actContext).getDAO();

        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        context = actContext;
        try {
            BaseFont base = BaseFont.createFont(BaseFont.HELVETICA, "Cp1254", BaseFont.EMBEDDED);
            fontHeader12 = new Font(base, 11f, Font.BOLD, BaseColor.BLACK);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepareBody(List<TodoList> todoLists) {
        for (TodoList todoList : todoLists) {
            Paragraph p = new Paragraph();
            p.setIndentationLeft(5);

            Paragraph firma = new Paragraph(new Paragraph(todoList.getListName(), fontHeader12));
            firma.setAlignment(Element.ALIGN_CENTER);
            firma.setSpacingAfter(20f);

            PdfPTable table = new PdfPTable(new float[]{1, 1, 3, 2, 2, 2});
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(context.getString(R.string.pdfColumnFirst));
            table.addCell(context.getString(R.string.pdfColumnSecond));
            table.addCell(context.getString(R.string.pdfColumnThird));
            table.addCell(context.getString(R.string.pdfColumnFourth));
            table.addCell(context.getString(R.string.pdfColumnFifth));
            table.addCell(context.getString(R.string.pdfColumnSixth));
            table.setHeaderRows(1);
            PdfPCell[] cells = table.getRow(0).getCells();
            for (PdfPCell cell : cells) {
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            }
            List<TodoListItem> todoListItems = dao.getTodoListItems(String.valueOf(todoList.getListId()));
            int i = 1;
            for (TodoListItem todoListItem : todoListItems) {
                table.addCell(String.valueOf(i));
                table.addCell(todoListItem.getListItemName());
                table.addCell(todoListItem.getListItemDesc());
                table.addCell(sdf.format(todoListItem.getListItemDeadline()));
                table.addCell(sdf.format(todoListItem.getListItemCreateDate()));
                Calendar deadlineDate = Calendar.getInstance();
                deadlineDate.setTime(todoListItem.getListItemDeadline());
                if (deadlineDate.before(Calendar.getInstance()))
                    table.addCell(context.getString(R.string.pdfStatusExpired));
                else
                    table.addCell(todoListItem.getListItemStatusCode() == 0 ? context.getString(R.string.pdfStatusContinued) : context.getString(R.string.pdfStatusCompleted));

                i++;
            }
            p.add(firma);
            p.add(table);
            try {
                document.add(p);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void initNewDocument() {
        documentPath = path + "/todo" + System.currentTimeMillis() + ".pdf";
        if (document != null && document.isOpen()) {
            document.close();
        }
        document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(documentPath));
        } catch (DocumentException e) {
            e.printStackTrace();
            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        document.setMargins(20, 20, 20, 20);
        document.open();
    }

    public void closeDocument() {
        document.close();
    }

    public String getDocumentPath() {
        return documentPath;
    }

}
