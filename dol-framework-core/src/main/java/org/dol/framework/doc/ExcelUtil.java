package org.dol.framework.doc;

import com.alibaba.fastjson.JSON;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dol.framework.util.FileUtil;
import org.dol.framework.util.ListUtil;
import org.dol.framework.util.StringUtil;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.*;

public abstract class ExcelUtil {

    /**
     *
     */
    private static final String ALIGN_CENTER_CELL_STYLE = "ALIGN_CENTER";
    /**
     *
     */
    private static final String ALIGN_LEFT_CELL_STYLE = "ALIGN_LEFT";
    /**
     *
     */
    private static final String ALIGN_RIGHT_CELL_STYLE = "ALIGN_RIGHT";
    /**
     *
     */
    private static final String FILE_2007 = ".xls";

    /**
     * 导出列表数据到指定位置
     *
     * @param title        要到处的Excel标题
     * @param headerMap    要导出的列标题
     * @param dataList     要导出的数据
     * @param clazz        导出的数据对应的class
     * @param saveFilePath 保存文件位置
     * @throws Exception
     */
    public static <E> void export(String title, Map<String, String> headerMap, List<E> dataList, Class<E> clazz, String saveFilePath) throws Exception {
        FileOutputStream fileOut = null;
        Workbook workbook = getWorkBook(saveFilePath);
        try {
            int rowIndx = 0;
            List<Field> fiels = getFieldsForExport(clazz, headerMap);
            Sheet sheet = null;
            if (StringUtils.hasText(title)) {
                sheet = workbook.createSheet(title);
            } else {
                sheet = workbook.createSheet();
            }
            Row row = null;
            if (StringUtils.hasText(title)) {
                CellStyle titleHeaderStyle = workbook.createCellStyle();
                setBorder(titleHeaderStyle);
                titleHeaderStyle.setFillBackgroundColor(new HSSFColor.DARK_BLUE().getIndex());
                titleHeaderStyle.setFillForegroundColor(new HSSFColor.WHITE().getIndex());
                titleHeaderStyle.setAlignment(CellStyle.ALIGN_CENTER);
                Font font = workbook.createFont();
                font.setBold(true);
                font.setFontHeightInPoints((short) 16);
                titleHeaderStyle.setFont(font);
                row = sheet.createRow(rowIndx++);
                // 在索引0的位置创建单元格（左上端）
                Cell cell = row.createCell(0, CellType.STRING);
                // 定义单元格为字符串类型
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, fiels.size() - 1));
                cell.setCellValue(title);
                cell.setCellStyle(titleHeaderStyle);

            }
            row = sheet.createRow(rowIndx++);
            CellStyle rowHeaderStyle = createCellStyle(workbook);
            for (int i = 0; i < fiels.size(); i++) {
                Field filed = fiels.get(i);
                Cell cell = row.createCell(i, CellType.STRING);
                cell.setCellValue(headerMap.get(filed.getName()));
                cell.setCellStyle(rowHeaderStyle);
            }
            for (E e : dataList) {
                row = sheet.createRow(rowIndx++);
                for (int i = 0; i < fiels.size(); i++) {
                    Field filed = fiels.get(i);
                    filed.setAccessible(true);
                    Cell cell = row.createCell(i, CellType.STRING);
                    Object object = filed.get(e);
                    if (object != null) {
                        cell.setCellValue(object.toString());
                    }
                    cell.setCellStyle(rowHeaderStyle);
                }
            }
            for (int i = 0; i < fiels.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            fileOut = new FileOutputStream(saveFilePath);
            workbook.write(fileOut);

        } finally {
            FileUtil.close(fileOut);
            FileUtil.close(workbook);
        }
    }

    public static void main(String[] args) throws IOException {
        List<Map<String, Object>> excelData = getExcelData("C:\\Users\\dolphin\\Desktop\\test.xlsx");
        System.out.println(JSON.toJSONString(excelData));

    }

    /**
     * 导出excel
     *
     * @param title        导出的excel的标题，可以为空
     * @param dataList     要到处的数据列表
     * @param saveFilePath 保存位置
     * @throws Exception
     */
    public static void export(String title, List<Map<String, Object>> dataList, String saveFilePath) throws Exception {
        FileOutputStream fileOut = null;
        Workbook workbook = getWorkBook(saveFilePath);
        try {
            int rowIndx = 0;
            Sheet sheet = null;
            if (StringUtils.hasText(title)) {
                sheet = workbook.createSheet(title);
            } else {
                sheet = workbook.createSheet();
            }
            Row row = null;

            Map<String, Object> firstRow = null;
            if (dataList == null || dataList.isEmpty()) {
                firstRow = new HashMap<String, Object>();
                firstRow.put("数据", "没有数据");
            } else {
                firstRow = dataList.get(0);
            }
            if (StringUtils.hasText(title)) {
                addTitleRow(title, workbook, sheet, rowIndx++, firstRow.size() - 1);
            }
            addHeaderRow(firstRow.keySet(), workbook, sheet, rowIndx++);

            if (dataList != null && !dataList.isEmpty()) {
                Map<String, CellStyle> rowCellStype = creatRowStyles(workbook);
                for (Map<String, Object> e : dataList) {
                    row = sheet.createRow(rowIndx++);
                    int colIndex = 0;
                    for (String key : firstRow.keySet()) {
                        addRowCell(sheet, rowCellStype, row, colIndex++, e.get(key));
                    }
                }
            }
            fileOut = new FileOutputStream(saveFilePath);
            workbook.write(fileOut);

        } finally {
            FileUtil.close(fileOut);
            FileUtil.close(workbook);
        }
    }

    public static void export(String title, List<Map<String, Object>> dataList, OutputStream out) throws Exception {
        Workbook workbook = getWorkBook(null);
        try {
            int rowIndx = 0;
            Sheet sheet = null;
            if (StringUtils.hasText(title)) {
                sheet = workbook.createSheet(title);
            } else {
                sheet = workbook.createSheet();
            }
            Row row = null;

            Map<String, Object> firstRow = null;
            if (dataList == null || dataList.isEmpty()) {
                firstRow = new HashMap<String, Object>();
                firstRow.put("数据", "没有数据");
            } else {
                firstRow = dataList.get(0);
            }
            if (StringUtils.hasText(title)) {
                addTitleRow(title, workbook, sheet, rowIndx++, firstRow.size() - 1);
            }
            addHeaderRow(firstRow.keySet(), workbook, sheet, rowIndx++);

            if (dataList != null && !dataList.isEmpty()) {
                Map<String, CellStyle> rowCellStype = creatRowStyles(workbook);
                for (Map<String, Object> e : dataList) {
                    row = sheet.createRow(rowIndx++);
                    int colIndex = 0;
                    for (String key : firstRow.keySet()) {
                        addRowCell(sheet, rowCellStype, row, colIndex++, e.get(key));
                    }
                }
            }

            workbook.write(out);

        } finally {
            FileUtil.close(workbook);
        }
    }

    public static void export(String title, List<String> headers, List<List<Object>> rows, String saveFile) throws Exception {
        Workbook workbook = null;
        FileOutputStream fileOut = null;
        try {
            workbook = buildExcel(title, headers, rows);
            fileOut = new FileOutputStream(saveFile);
            workbook.write(fileOut);
        } finally {
            FileUtil.close(fileOut);
            FileUtil.close(workbook);
        }
    }

    public static void export(String title, List<String> headers, List<List<Object>> rows, OutputStream out) throws Exception {
        Workbook workbook = null;
        try {
            workbook = buildExcel(title, headers, rows);
            workbook.write(out);
        } finally {
            FileUtil.close(workbook);
        }
    }

    public static Workbook buildExcel(String title, List<String> headers, List<List<Object>> rows) throws Exception {
        Workbook workbook = getWorkBook(null);
        Sheet sheet = null;
        if (StringUtils.hasText(title)) {
            sheet = workbook.createSheet(title);
        } else {
            sheet = workbook.createSheet();
        }

        int rowIndx = 0;

        if (StringUtils.hasText(title)) {
            int margedRows = headers.size() - 1;
            addTitleRow(title, workbook, sheet, rowIndx++, margedRows);
        }

        if (ListUtil.isNotNullAndEmpty(headers)) {
            addHeaderRow(headers, workbook, sheet, rowIndx++);
        }

        if (ListUtil.isNotNullAndEmpty(rows)) {
            Map<String, CellStyle> rowCellStype = creatRowStyles(workbook);
            int columnCount = 0;
            for (List<Object> rowData : rows) {
                columnCount = Math.max(columnCount, rowData.size());
                Row row = sheet.createRow(rowIndx++);
                int colIndex = 0;
                for (Object obj : rowData) {
                    addRowCell(sheet, rowCellStype, row, colIndex++, obj);
                }
            }
            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
            }
        }
        // 新建一输出文件流
        // 把相应的Excel 工作簿存盘
        return workbook;

    }

    /**
     * 参照方法名.
     *
     * @return
     */
    private static Workbook getWorkBook(String fileName) {
        if (fileName == null) {
            return getDefaultWorkBook();
        }
        return fileName.toLowerCase().endsWith(FILE_2007) ? new HSSFWorkbook() : new XSSFWorkbook();
    }

    /**
     * 参照方法名.
     *
     * @return
     */
    private static HSSFWorkbook getDefaultWorkBook() {
        return new HSSFWorkbook();
    }

    /**
     * 参照方法名.
     *
     * @param sheet
     * @param rowCellStyles
     * @param row
     * @param colIndex
     * @param object
     */
    private static void addRowCell(Sheet sheet, Map<String, CellStyle> rowCellStyles, Row row, int colIndex, Object object) {
        Cell cell = row.createCell(colIndex, CellType.STRING);

        if (object != null) {
            if (object instanceof Date) {
                cell.setCellValue((Character) object);
                cell.setCellStyle(rowCellStyles.get(ALIGN_CENTER_CELL_STYLE));
            } else if (object instanceof Boolean) {
                cell.setCellValue((Boolean) object);
                cell.setCellStyle(rowCellStyles.get(ALIGN_CENTER_CELL_STYLE));
            } else if (object instanceof Short || object instanceof Byte || object instanceof Integer) {
                cell.setCellValue(((Number) object).doubleValue());
                cell.setCellStyle(rowCellStyles.get(ALIGN_RIGHT_CELL_STYLE));
            } else if (object instanceof BigInteger) {
                cell.setCellValue(object.toString());
                cell.setCellStyle(rowCellStyles.get(ALIGN_RIGHT_CELL_STYLE));
            } else {
                cell.setCellValue(object.toString());
                cell.setCellStyle(rowCellStyles.get(ALIGN_LEFT_CELL_STYLE));
            }
        } else {
            cell.setCellValue(StringUtil.EMPTY_STRING);
            cell.setCellStyle(rowCellStyles.get(ALIGN_LEFT_CELL_STYLE));
        }
    }

    /**
     * 参照方法名.
     *
     * @param workbook
     * @return
     */
    private static Map<String, CellStyle> creatRowStyles(Workbook workbook) {

        Map<String, CellStyle> map = new HashMap<String, CellStyle>();

        CellStyle rowCellStype = createCellStyle(workbook);
        rowCellStype.setAlignment(CellStyle.ALIGN_RIGHT);
        map.put(ALIGN_RIGHT_CELL_STYLE, rowCellStype);

        rowCellStype = createCellStyle(workbook);
        rowCellStype.setAlignment(CellStyle.ALIGN_LEFT);
        map.put(ALIGN_LEFT_CELL_STYLE, rowCellStype);

        rowCellStype = createCellStyle(workbook);
        rowCellStype.setAlignment(CellStyle.ALIGN_CENTER);
        map.put(ALIGN_CENTER_CELL_STYLE, rowCellStype);

        return map;
    }

    /**
     * 参照方法名.
     *
     * @param workbook
     * @return
     */
    private static CellStyle createCellStyle(Workbook workbook) {
        CellStyle rowCellStype = workbook.createCellStyle();
        rowCellStype.setFillBackgroundColor(new HSSFColor.GREY_80_PERCENT().getIndex());
        rowCellStype.setFillForegroundColor(new HSSFColor.WHITE().getIndex());
        setBorder(rowCellStype);
        return rowCellStype;
    }

    /**
     * 参照方法名.
     *
     * @param headers
     * @param workbook
     * @param sheet
     * @param rowIndx
     */
    private static void addHeaderRow(Collection<String> headers, Workbook workbook, Sheet sheet, int rowIndx) {
        CellStyle rowHeaderStyle = creaHeaderRowStyle(workbook);
        int colIndex = 0;
        Row headerRow = sheet.createRow(rowIndx);
        for (String header : headers) {
            Cell cell = headerRow.createCell(colIndex++, CellType.STRING);
            cell.setCellValue(header);
            cell.setCellStyle(rowHeaderStyle);
        }
    }

    /**
     * 参照方法名.
     *
     * @param workbook
     * @return
     */
    private static CellStyle creaHeaderRowStyle(Workbook workbook) {
        CellStyle rowHeaderStyle = createCellStyle(workbook);
        setBorder(rowHeaderStyle);
        return rowHeaderStyle;
    }

    /**
     * 参照方法名.
     *
     * @param title
     * @param workbook
     * @param sheet
     * @param rowIndx
     * @param margedRows
     */
    private static void addTitleRow(String title, Workbook workbook, Sheet sheet, int rowIndx, int margedRows) {
        Row row = sheet.createRow(rowIndx);
        CellStyle titleHeaderStyle = workbook.createCellStyle();
        titleHeaderStyle.setFillBackgroundColor(new HSSFColor.DARK_BLUE().getIndex());
        titleHeaderStyle.setFillForegroundColor(new HSSFColor.WHITE().getIndex());
        titleHeaderStyle.setAlignment(CellStyle.ALIGN_CENTER);
        setBorder(titleHeaderStyle);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        titleHeaderStyle.setFont(font);
        // 在索引0的位置创建单元格（左上端）
        Cell cell = row.createCell(0, CellType.STRING);
        // 定义单元格为字符串类型
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, margedRows));
        cell.setCellValue(title);
        cell.setCellStyle(titleHeaderStyle);
    }

    private static void setBorder(CellStyle rowHeaderStyle) {
        rowHeaderStyle.setBorderBottom((short) 1);
        rowHeaderStyle.setBorderLeft((short) 1);
        rowHeaderStyle.setBorderRight((short) 1);
        rowHeaderStyle.setBorderTop((short) 1);
    }

    private static <E> List<Field> getFieldsForExport(Class<E> clazz, Map<String, String> headerMap) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> fieldList = new ArrayList<Field>();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            if (fieldType.isPrimitive() || Number.class.isAssignableFrom(fieldType) || Date.class.isAssignableFrom(fieldType)
                    || Calendar.class.isAssignableFrom(fieldType)
                    || Character.class.isAssignableFrom(fieldType) || CharSequence.class.isAssignableFrom(fieldType)) {

                if (headerMap != null) {
                    if (headerMap.containsKey(field.getName())) {
                        fieldList.add(field);
                    }
                } else {
                    fieldList.add(field);
                }
            }

        }
        return fieldList;
    }

    public static List<Map<String, Object>> getExcelData(String fileName) throws IOException {
        InputStream inputStream = new FileInputStream(fileName);
        return getExcelData(inputStream, fileName.endsWith(".xlsx"));
    }

    public static List<Map<String, Object>> getExcelData(byte[] data, Boolean xlsx) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(data);
        return getExcelData(inputStream, xlsx);
    }

    public static List<Map<String, Object>> getExcelData(InputStream inputStream, Boolean xlsx) throws IOException {
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        Workbook workbook = null;
        try {
            workbook = getWorkBook(inputStream, xlsx);
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getFirstRowNum() >= 0) {
                int firstRowNum = sheet.getFirstRowNum();
                int lastRowNum = sheet.getLastRowNum();
                Row firstRow = sheet.getRow(sheet.getFirstRowNum());
                HashMap<Integer, String> columnNames = new HashMap<Integer, String>();
                for (Cell cell : firstRow) {
                    columnNames.put(cell.getColumnIndex(), cell.getStringCellValue());
                }
                for (int i = (firstRowNum + 1); i <= lastRowNum; i++) {
                    Row row = sheet.getRow(i);
                    Map<String, Object> dataRow = new HashMap<String, Object>();
                    for (Cell cell : row) {
                        Object cellValue = getCellValue(cell);
                        dataRow.put(columnNames.get(cell.getColumnIndex()), cellValue);
                    }
                    datas.add(dataRow);
                }
            }
        } finally {
            FileUtil.close(workbook);
        }
        return datas;
    }

    private static Workbook getWorkBook(InputStream inputStream, boolean xlsx) throws IOException {
        Workbook workbook = xlsx ? new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream);
        return workbook;
    }

    private static Object getCellValue(Cell cell) {

        CellType cellType = cell.getCellTypeEnum();
        return getValue(cell, cellType);
    }

    private static Object getValue(Cell cell, CellType cellType) {
        switch (cellType) {
            case BLANK:
                return cell.getStringCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                CellType formulaResultType = cell.getCachedFormulaResultTypeEnum();
                return getValue(cell, formulaResultType);
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
            case ERROR:
                return cell.getStringCellValue();
            default:
                return cell.getStringCellValue();
        }
    }
}
