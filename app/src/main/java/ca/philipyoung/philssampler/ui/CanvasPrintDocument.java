package ca.philipyoung.philssampler.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class CanvasPrintDocument extends PrintDocumentAdapter {
    /*
     Android users frequently view content solely on their devices, but there
     are times when showing someone a screen is not an adequate way to
     share information. Being able to print information from your Android
     application gives users a way to see a larger version of the content
     from your app or share it with another person who is not using your
     application. Printing also allows them to create a snapshot of
     information that does not depend on having a device, sufficient battery
     power, or a wireless network connection.
     Requires Android 4.4 (API Level 19) or higher
    */
    public final static String CALENDAR_KEY = "calendar";
    // PDF drawing parameters
    public static final int REPORT_DPI = 720;

    private Context mContext;
    /*
        Canvas grid is print size dependent.
        For Letter-Portrait, the canvas size is : 792×612
        For Letter-Landscape, the canvas size is : 612×792
        For Legal-Portrait, the canvas size is : 1008×612
        For Legal-Landscape, the canvas size is : 612×1008
        For A4-Portrait, the canvas size is : 841×595
        For Index Card 3×5-Landscape, the canvas size is : 216×360

        A ¾" border is 54 units from the edge.
     */
    /*
        For a calendar, build an 8×7 / 2¾×2¾  matrix that can shrink if minimum height/width requires

     */

    // Set up the grid by defining the horizontal and vertical borders
    private double[] dblsVer, dblsHor;
    // These calendar borders are fixed relative to each other, independent of paper size and orientation.
    // Use these calendar borders to help build the page borders. Each paper size is different.
    private double[] dblsCalHor = new double[]{
            0.0,
            18.0,
            36.0,
            54.0,
            72.0,
            90.0,
            108.0,
            126.0,
            144.0
    }, dblsCalVer = new double[]{
            0.0,
            20.6,
            41.1,
            61.7,
            82.3,
            102.9,
            123.4,
            144.0
    };
    private Double dblCalMarginVer = 18.0,
            dblCalMarginHor = 18.0;
    private int pageHeight, pageWidth, intTotalPages, intCalendarColumns, intCalendarRows;
    private ArrayList<Integer> writtenPageArray;
    private String strReportType;
    private Calendar calCalendar;
    private Calendar[] calendars;
    private PdfDocument rptPdfDocument;

    public CanvasPrintDocument(Context context) {
        this.mContext = context;
        this.strReportType = CALENDAR_KEY;
        this.calCalendar = Calendar.getInstance();
        this.calCalendar.set(this.calCalendar.get(Calendar.YEAR), 0, 1);
    }

    public CanvasPrintDocument(Context context, String reportType) {
        this.mContext = context;
        this.strReportType = reportType;
        if (this.strReportType.equals(CALENDAR_KEY)) {
            this.calCalendar = Calendar.getInstance();
            this.calCalendar.set(this.calCalendar.get(Calendar.YEAR), 0, 1);
        }
    }

    public CanvasPrintDocument(Context context, String reportType, Integer year) {
        this.mContext = context;
        this.strReportType = reportType;
        if (this.strReportType.equals(CALENDAR_KEY)) {
            this.calCalendar = Calendar.getInstance();
            this.calCalendar.set(year, 0, 1);
        }
    }

    public CanvasPrintDocument(Context context, String reportType, Date date) {
        this.mContext = context;
        this.strReportType = reportType;
        if (this.strReportType.equals(CALENDAR_KEY)) {
            this.calCalendar = Calendar.getInstance();
            this.calCalendar.setTime(date);
        }
    }

    public CanvasPrintDocument(Context context, String reportType, Calendar calendar) {
        this.mContext = context;
        this.strReportType = reportType;
        if (this.strReportType.equals(CALENDAR_KEY)) {
            this.calCalendar = Calendar.getInstance();
            this.calCalendar.setTime(calendar.getTime());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        intTotalPages = 0;
         writtenPageArray = new ArrayList<>();
        this.calendars = new Calendar[12];
        for (int i = 0; i < 12; i++) {
            calendars[i] = Calendar.getInstance();
            calendars[i].set(calCalendar.get(Calendar.YEAR), i, 1);
        }
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        rptPdfDocument = new PrintedPdfDocument(mContext, newAttributes);

        if (newAttributes.getMediaSize() != null) {
            if (newAttributes.getMediaSize().isPortrait()) {
                pageHeight =
                        newAttributes.getMediaSize().getHeightMils() * 72 / 1000;
                pageWidth =
                        newAttributes.getMediaSize().getWidthMils() * 72 / 1000;
            } else {
                pageHeight =
                        newAttributes.getMediaSize().asLandscape().getHeightMils() * 72 / 1000;
                pageWidth =
                        newAttributes.getMediaSize().asLandscape().getWidthMils() * 72 / 1000;
            }

            if (this.strReportType.equals(CALENDAR_KEY)) {
                // count how many 2"×2" calendars can fit on a page
                if (pageHeight - 2 * 54 > 2000 * 72 / 1000 && pageWidth - 2 * 54 > 2000 * 72 / 1000) {
                    // it fits at least one calendar per page
                    int intCalendarsPerPage;
                    double dblCalendarHeight = (2 + 0.25) * 72,
                            dblCalendarWidth = (2 + 0.25) * 72;
                    intCalendarColumns = (int) ((pageWidth- 2 * 54+18) / dblCalendarWidth);
                    intCalendarRows = (int) ((pageHeight- 2 * 54+18) / dblCalendarHeight);
                    intCalendarsPerPage = intCalendarRows * intCalendarColumns;
                    intTotalPages = (int) (12.0 / intCalendarsPerPage + 11.0 / 12.0);
                }
            }
            this.writtenPageArray=new ArrayList<>();
        }

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        if (intTotalPages > 0) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            // Return print information to print framework
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder(String.format(Locale.US, "calendar_%s.pdf", timeStamp))
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(intTotalPages);
            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        } else {
            callback.onLayoutFailed("The selected page size is too small to show any data");
        }

    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        for (int i = 0; i < intTotalPages; i++) {
            // Check to see if this page is in the output range.
            if (containsPage(pages, i)) {
                this.writtenPageArray.add(i);
                PdfDocument.PageInfo newPage =
                        new PdfDocument.PageInfo.Builder(
                                pageWidth, pageHeight, i
                        ).create();
                PdfDocument.Page page = rptPdfDocument.startPage(newPage);
                // check for cancellation
                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    rptPdfDocument.close();
                    rptPdfDocument = null;
                    return;
                }
                draw_calendars(page, i);
                rptPdfDocument.finishPage(page);
            }
        }
        // Write PDF document to file
        try {
            rptPdfDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        }
        // Signal the print framework the document is complete
        callback.onWriteFinished(pages);
    }

    @Override
    public void onFinish() {
        super.onFinish();
        rptPdfDocument.close();
        rptPdfDocument = null;
    }

    private void drawCentredText(Canvas canvas, String text, float floatX, float floatY, Paint paint) {
        // pull back half the width of the text, and drop it half the height of text
        Rect rect = new Rect();
        paint.getTextBounds(text,0,text.length(),rect);
        float fltWidth = paint.measureText(text),
                fltHeight =  rect.height();
        float fltPathStart = floatX - fltWidth / 2.0f;
        float fltPathEnd = fltPathStart + fltWidth;
        floatY += fltHeight / 2.0f;
        Path path = new Path();
        path.moveTo(fltPathStart, floatY);
        path.lineTo(fltPathEnd, floatY);

        // canvas.drawLine(fltPathStart,floatY-paint.getTextSize(),fltPathEnd,floatY-paint.getTextSize(),paint);
        // canvas.drawLine(fltPathStart,floatY,fltPathEnd,floatY,paint);

        canvas.drawTextOnPath(text, path, 0, 0, paint);
    }

    private void draw_calendars(PdfDocument.Page page, int pageKey) {
        int pageNumber = pageKey + 1;
        Canvas canvas = page.getCanvas();
        PdfDocument.PageInfo pageInfo = page.getInfo();
        // units are in points (1/72 of an inch)
        int titleBaseLine = 72;
        int leftMargin = 54;
        float calendarWidth = 144, calendarSpacingWidth;
        float calendarHeight = 144, calendarSpacingHeight;
        calendarSpacingWidth=calendarWidth+(float)(this.pageWidth -leftMargin*2-calendarWidth*this.intCalendarColumns)/(float)(this.intCalendarColumns-1);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(36);
        String strTitle = String.format(Locale.US, "%1$tY", this.calCalendar);
        // find the halfway point across the screen
        float fltHalfway = this.pageWidth / 2.0f;
        drawCentredText(canvas, strTitle, fltHalfway, (float) titleBaseLine - 18.0f, paint);

        int intMonth = pageKey * this.intCalendarRows * this.intCalendarColumns;
        for (int iRow = 0; iRow < this.intCalendarRows; iRow++) {
            for (int jCol = 0; jCol < this.intCalendarColumns; jCol++) {
                if (intMonth < 12) {
                    // Draw a calendar
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(calendars[intMonth].getTime());
                    int intCalTop = titleBaseLine + 18 + iRow * 162;
                    int intCalLeft = leftMargin + (int)(jCol * calendarSpacingWidth);
                    int intCalBottom = (int) dblsCalHor[dblsCalHor.length - 1] + intCalTop;
                    int intCalRight = (int) dblsCalVer[dblsCalVer.length - 1] + intCalLeft;
                    float intCalFirstRow = (float) dblsCalHor[2] + intCalTop,
                            fltMidPointY;
                    // canvas.drawRect(intCalLeft, intCalTop, intCalRight, intCalBottom, paint);
                    canvas.drawLine(intCalLeft, intCalTop, intCalLeft, intCalBottom, paint);
                    canvas.drawLine(intCalRight, intCalTop, intCalRight, intCalBottom, paint);
                    canvas.drawLine(intCalLeft, intCalTop, intCalRight, intCalTop, paint);
                    for (int i = 2; i < dblsCalHor.length; i++) {
                        canvas.drawLine(intCalLeft, (float) dblsCalHor[i] + intCalTop, intCalRight, (float) dblsCalHor[i] + intCalTop, paint);
                    }
                    for (int j = 1; j < dblsCalVer.length - 1; j++) {
                        canvas.drawLine((float) dblsCalVer[j] + intCalLeft, intCalFirstRow, (float) dblsCalVer[j] + intCalLeft, intCalBottom, paint);
                    }
                    String strMonth = String.format(Locale.US, "%1$tB", calendar),
                            strCellText;
                    paint.setTextSize(15.0f);
                    fltMidPointY = (float) (dblsCalHor[1] + dblsCalHor[0]) / 2.0f + intCalTop;
                    drawCentredText(canvas, strMonth, (float) (dblsCalVer[dblsCalVer.length - 1] + dblsCalVer[0]) / 2.0f + intCalLeft, fltMidPointY, paint);
                    do {
                        int intColumn = calendar.get(Calendar.DAY_OF_WEEK); // A value 1 to 7, localized
                        int intRow = calendar.get(Calendar.WEEK_OF_MONTH);  // A value 1 to 6
                        strCellText = String.format(Locale.US, "%1$te", calendar);
                        fltMidPointY = (float) (dblsCalHor[intRow + 2] + dblsCalHor[intRow + 1]) / 2.0f + intCalTop;
                        drawCentredText(canvas, strCellText, (float) (dblsCalVer[intColumn] + dblsCalVer[intColumn - 1]) / 2.0f + intCalLeft, fltMidPointY, paint);
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    } while (String.format(Locale.US, "%1$tB", calendar).equals(strMonth));
                    for (int i = 0; i < 7; i++) {
                        int intColumn = calendar.get(Calendar.DAY_OF_WEEK); // A value 1 to 7, localized
                        int intRow = 0;
                        strCellText = String.format(Locale.US, "%1$ta", calendar).substring(0, 1);
                        fltMidPointY = (float) (dblsCalHor[intRow + 2] + dblsCalHor[intRow + 1]) / 2.0f + intCalTop;
                        drawCentredText(canvas, strCellText, (float) (dblsCalVer[intColumn] + dblsCalVer[intColumn - 1]) / 2.0f + intCalLeft, fltMidPointY, paint);
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    // Go build next month
                    intMonth++;
                }
            }
        }
    }

    private boolean containsPage(PageRange[] pageRanges, int page) {
        for (int i = 0; i < pageRanges.length; i++) {
            if ((page >= pageRanges[i].getStart()) &&
                    (page <= pageRanges[i].getEnd()))
                return !this.writtenPageArray.contains(page);
        }
        return false;
    }
}
