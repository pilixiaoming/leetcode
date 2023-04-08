package databricks;

//https://www.1point3acres.com/bbs/thread-781585-1-1.html
//
//import com.sun.rowset.internal.Row;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Stream;
//import java.util.stream.Collectors;
///**
// * A CSVViwer represents a view of a csv file, backed by Stream<Row> data
// * columnNames
// */
//public class CSVViewer {
//// Column Names for this CSVViewer
//private List<String> columnNames;
//// Contains CSVViewer data.
//private Stream<Row> data;
//// fileName could be empty if it not constructed from reading a file.
//private String fileName = "";
///**
//      * Reads from a given path and construct CSVViewer.
//      *
//      * @param path
//      * @throws IOException
//      */
//public CSVViewer(String path) throws IOException {
//fileName = path;
//Stream<String> dataLines = Files.lines(Paths.get(fileName));
//String firstLine = dataLines.iterator().next();
//columnNames = Arrays.asList(firstLine.split(","));
//data = dataLines.map(Row::new);
//}
///**
//      * Contructs a CSVViewer from columnNames and data.
//      *
//      * @param columnNames
//      * @param data
//      * @throws IOException
//      */
//public CSVViewer(List<String> columnNames, Stream<Row> data) {
//this.columnNames = columnNames;
//this.data = data;
//}
///**
//      * Print ColumnNames in one line.
//      */
//public List<String> getColumnNames() {
//return columnNames;
//}
///**
//      * Print ColumnNames first, followed by all the data rows.
//      */
//public List<String> getData() {
//return data.map(Row::toString).collect(Collectors.toList());
//}
///**
//      * Truncates data to length.
//      *
//      * @param length the final size truncates to.
//      * [url=home.php?mod=space&uid=160137]@return[/url] this.
//      */
//public CSVViewer take(int length) {
//assert length > 0 : "TAKE length should be larger than 0.";
//this.data = data.limit(length);
//return this;
//}
///**
//      * Truncates data to length.
//      *
//      * @param cols a list of column names to be selected.
//      */
//public CSVViewer select(List<String> cols) {
//List<Integer> colIndex = cols.stream().map(col -> getColIndex(col)).collect(Collectors.toList());
//this.columnNames = cols;
//this.data = data.map(row -> {
//List<String> list = new ArrayList<>();
//for (int i = 0; i < colIndex.size(); i++) {
//list.add(row.get(colIndex.get(i)));
//}
//return new Row(list);
//});
//return this;
//}
///**
//      * Order by colName.
//      *
//      * @param colName
//      * @return this.
//      */
//public CSVViewer orderby(String colName) {
//final int sortIndex = getColIndex(colName);
//this.data = data.sorted((row1, row2) -> Row.compareAtIndex(row1, row2, sortIndex));
//return this;
//}
///**
//      * Countby colName
//      *
//      * @param colName
//      * @return this.
//      */
//public CSVViewer countby(String colName) {
//final int colIndex = getColIndex(colName);
//Map<String, Long> map = data.collect(Collectors.groupingBy(row -> row.get(colIndex), Collectors.counting()));
//this.columnNames = Arrays.asList(colName, "count");
//this.data = map.entrySet().stream().map(entry -> new Row(entry.getKey(), Long.toString(entry.getValue())));
//return this;
//}
///**
//      * Performs a left join with the joinFile.
//      *
//      * @param joinFile
//      * @param joinCol
//      * @return this.
//      */
//public CSVViewer leftjoin(CSVViewer joinFile, String joinCol) {
//final int joinColIndex = joinFile.getColIndex(joinCol);
//Map<String, Row> map = joinFile.data.collect(Collectors.toMap(row -> row.get(joinColIndex), row -> {
//List<String> newCols = new ArrayList<>();
//for (int j = 0; j < row.columns.length; j++) {
//if (j != joinColIndex) {
//newCols.add(row.get(j));
//}
//}
//return new Row(newCols);
//}, (left, right) -> left));
//List<String> colNamesList = new ArrayList<>();
//colNamesList.addAll(columnNames);
//colNamesList
//.addAll(joinFile.columnNames.stream().filter(col -> !col.equals(joinCol)).collect(Collectors.toList()));
//this.columnNames = colNamesList;
//final int colIndex = getColIndex(joinCol);
//this.data = data.map(row -> {
//List<String> newCols = new ArrayList<>();
//newCols.addAll(Arrays.asList(row.columns));
//if (map.containsKey(row.get(colIndex))) {
//newCols.addAll(Arrays.asList(map.get(row.get(colIndex)).columns));
//} else {
//for (int i = newCols.size(); i < colNamesList.size(); i++) {
//newCols.add("");
//}
//}
//return new Row(newCols);
//});
//return this;
//}
///**
//      * Performs a sort merge join with the joinFile.
//      *
//      * @param joinFile
//      * @param joinCol
//      * @return this.
//      */
//public CSVViewer sortMergeJoin(CSVViewer joinFile, String joinCol) {
//this.orderby(joinCol);
//joinFile = joinFile.orderby(joinCol);
//final int colIndex1 = getColIndex(joinCol);
//final int colIndex2 = joinFile.getColIndex(joinCol);
//‍‍‍‍‌‌‌‍‌‍‍‍‌‍‌‍‍‌‌‍   List<String> colNamesList = new ArrayList<>();
//colNamesList.addAll(columnNames);
//colNamesList
//.addAll(joinFile.columnNames.stream().filter(col -> !col.equals(joinCol)).collect(Collectors.toList()));
//this.columnNames = colNamesList;
//Stream<Row> joinedRows = Stream.of();
//Iterator<Row> iter1 = this.data.iterator();
//Iterator<Row> iter2 = joinFile.data.iterator();
//Row row2 = iter2.hasNext() ? iter2.next() : null;
//while (iter1.hasNext()) {
//Row row1 = iter1.next();
//// Advance row2 to row1 or pass row1 if possible.
//int cmp = -1;
//while (row2 != null) {
//cmp = row1.get(colIndex1).compareTo(row2.get(colIndex2));
//if (cmp >= 0) {
//break;
//} else {
//if (iter2.hasNext()) {
//row2 = iter2.next();
//} else {
//row2 = null;
//}
//}
//}
//List<String> cols = new ArrayList<>();
//cols.addAll(Arrays.asList(row1.columns));
//if (row2 != null && cmp == 0) {
//for (int i = 0; i < row2.columns.length; i++) {
//if (i != colIndex2) {
//cols.add(row2.columns[i]);
//}
//}
//} else {
//for (int i = cols.size(); i < colNamesList.size(); i++) {
//cols.add("");
//}
//}
//joinedRows = Stream.concat(joinedRows, Stream.of(new Row(cols)));
//}
//this.data = joinedRows;
//return this;
//}
//// Returns the col index of specified colName.
//// Throws AssertionError if colName is not found.
//// NOTE: this is used by SELECT/SORT/ORDERBY/COUNTBY/JOIN to throw if columnName
//// doesn't exist.
//private int getColIndex(String colName) {
//for (int i = 0; i < columnNames.size(); i++) {
//if (columnNames.get(i).equals(colName)) {
//return i;
//}
//}
//throw new AssertionError(String.format("Cannot find COLUMN %s in %s", colName, fileName));
//}
//}
