/**
 *    Copyright 2015-2017 donghyuck
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package architecture.community.util.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * </p> 간단한 엑셀 생성 유틸리티 </p>
 * 
 * 
 * XSSFExcelWriter writer = new XSSFExcelWriter(); </br>
 * 	// 작업할 시트를 추가한다.  	 </br></br>
 * 
 * 	writer.createSheet("대상자 목록"); </br>
 * 	// 첫번째 ROW 를 컬럼으로 사용하며 컬럼 인텍스 , 컬럼 이름, 값 데이터 추출을 위한 키, 마지막으로 필요시 컬럼의 폭을 지정한다.</br></br>
 * 	 
 * 	writer.setColumn(0, "대상자여부", "EXCEL_CHECKFLAG", 3000);</br>
 * 	writer.setColumn(1, "학과", "DVS_NAME", 5000);</br>
 * 	writer.setColumn(2, "학번", "ID", 4000);</br>
 * 	writer.setColumn(3, "성명", "NAME", 4000);</br>
 * 	writer.setColumn(4, "학년", "CURRENT_CLASS", 3000);</br>
 * 	writer.setHeaderToFirstRow(); </br></br>
 * 
 *  // 데이터 쓰기를 위한 데이터를 설정한다. 첫번째 ROW 다음부터 앞에서 지정한 컬럼 정보에 따라 쓰기를 진행한다. </br>
 * 	writer.setData(items);</br></br>
 *  <p>sheet 를 추가하는 경우 아래와 같이 위의 작업을 반복하면 된다. </>
 * 	writer.createSheet("대상자 목록2"); </br>
 * 	// 첫번째 ROW 를 컬럼으로 사용하며 컬럼 인텍스 , 컬럼 이름, 값 데이터 추출을 위한 키, 마지막으로 필요시 컬럼의 폭을 지정한다.</br></br>
 * 	 
 * 	writer.setColumn(0, "대상자여부", "EXCEL_CHECKFLAG", 3000);</br>
 * 	writer.setColumn(1, "학과", "DVS_NAME", 5000);</br>
 * 	writer.setColumn(2, "학번", "ID", 4000);</br>
 * 	writer.setColumn(3, "성명", "NAME", 4000);</br>
 * 	writer.setColumn(4, "학년", "CURRENT_CLASS", 3000);</br>
 * 	writer.setHeaderToFirstRow(); </br></br>
 * 
 *  // 데이터 쓰기를 위한 데이터를 설정한다. 첫번째 ROW 다음부터 앞에서 지정한 컬럼 정보에 따라 쓰기를 진행한다. </br>
 * 	writer.setData(items);</br></br>
 * 	// 이함수를 호출해야 컬럼 폭을 설정에 따라 적용한다. (BUG)</br>
 * 	writer.setColumnsWidth();</br></br>
 * 
 * 	FileOutputStream fileOutStream = new FileOutputStream(new File("원하는 파일 경로"));</br>
 * 	writer.write(fileOutStream);</br></br>
 * 
 * 
 * @author donghyuck
 *
 */
public class XSSFExcelWriter {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private XSSFWorkbook workbook;

	private int sheetIndex = 0;
	
	private Map<Integer, List<Column>> columnsList ;
	  
	private XSSFCellStyle cellStyle;
	
	private static final String EMPTY_STRING = "";
	
	public XSSFExcelWriter() {
		this.workbook = new XSSFWorkbook();
		columnsList = new HashMap<Integer, List<Column>>();
	}
	
	protected List<Column> getColumnList(boolean CreateIfNotExist){
		if( columnsList.get(getSheetIndex()) == null && CreateIfNotExist ){
			List<Column> listToUse = new ArrayList<Column>();
			columnsList.put(getSheetIndex(), listToUse);
		}		
		return columnsList.get(getSheetIndex());
	}
	
	
	public XSSFCellStyle getCellStyle() {
		if( cellStyle == null )
			cellStyle = this.createCellStyle();
		return cellStyle;
	}

	public void setCellStyle(XSSFCellStyle cellStyle) {
		this.cellStyle = cellStyle;
	}

	public XSSFWorkbook getWorkbook() {
		return workbook;
	}

	public int getSheetIndex() {
		return sheetIndex;
	}

	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	public void setWorkbook(XSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	public void createSheet(String name) {
		XSSFSheet sheet = this.workbook.createSheet(name);
		this.sheetIndex = workbook.getSheetIndex(sheet);
	}
	
	public XSSFRow addRow(int rownum) {
		XSSFSheet sheet = getSheetAt(getSheetIndex());
		return sheet.createRow(rownum);
	}
	
	public XSSFCell addCell(int rownum, int column, XSSFCellStyle cellStyle) {
		XSSFCell cell = getRow(rownum).createCell(column);		
		if( cellStyle != null)
			cell.setCellStyle(cellStyle);
		
		return cell;
	}

	public XSSFSheet getSheetAt(int sheetIndex) {
		return workbook.getSheetAt(sheetIndex);
	}
    
    public int getFirstRowNum() {
    	return getSheetAt(getSheetIndex()).getFirstRowNum();
       }

	public XSSFRow getRow(int rownum) {
		return getSheetAt(getSheetIndex()).getRow(rownum);
	}
	
	private XSSFCellStyle createCellStyle() {
		XSSFCellStyle style = workbook.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.index);		
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.index);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.index);
		style.setBorderTop(BorderStyle.THIN);
		// style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
		style.setTopBorderColor(IndexedColors.BLACK.index);
		// style.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);		
		return style;
	}
	
	public void setHeaderToFirstRow () {		
		int rowNum = getFirstRowNum();
		XSSFRow row = addRow(rowNum);
		XSSFCellStyle cellStyle = createCellStyle();
		cellStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);		
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		cellStyle.setFont(font);		
		for (Column c : getColumnList(true)) {
			XSSFCell cell = getRow(row.getRowNum()).createCell(c.columnIndex);	
			
			cell.setCellStyle(cellStyle);
			cell.setCellValue(c.name);
		}		
	}

	public int getLastRowNum() {
		return getSheetAt(getSheetIndex()).getLastRowNum();
	}

	public void setData(List<Map<String,Object>>  items ) {
		if( items != null ){
			for( Map<String,Object> item : items ){
				setDataToRow(item);
			}
		}
	}	
	
	public void setDataToRow(Map<String, Object> data ) {		
		XSSFRow row = addRow(getLastRowNum() + 1);
		for( Column c : getColumnList(true) )
		{
			XSSFCell cell = addCell(row.getRowNum(), c.columnIndex, getCellStyle());
			Object value = data.get(c.valueKey);	
			cell.setCellType(CellType.STRING);	
			if( value != null)
				cell.setCellValue( value.toString().trim());		
			else
				cell.setCellValue(EMPTY_STRING);
		}
    }
	

	public void write(File file) {
		FileOutputStream fs = null;
		try {
			// File.createTempFile(prefix, suffix);
			fs = new FileOutputStream(file);
			workbook.write(fs);
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		} finally {
			if (fs != null)
				try {
					fs.close();
				} catch (IOException e) {
					log.error(e.getMessage(),e);
				}
		}
	}

	public void write(OutputStream output) {
		try {
			workbook.write(output);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
		}
	}
	
	public void setColumn(int idx, String name, String dataKey ){
		getColumnList(true).add(new Column(idx, name, dataKey));
	}
	
	public void setColumn(int idx, String name, String dataKey, int width ){
		getColumnList(true).add(new Column(idx, name, dataKey, width));
	}
	
	
	public void setColumnsWidth(){
		for (Column c : getColumnList(true)) {
			if(c.width > 0){
				getSheetAt(getSheetIndex()).setColumnWidth(c.columnIndex, c.width);
			}
		}
	}
	
	public static class Column {		
		String name;		
		String valueKey;		
		int columnIndex;		
		int width = 0 ;		
		public Column(int columnIndex, String name, String valueKey) {
			super();
			this.columnIndex = columnIndex;
			this.name = name;
			this.valueKey = valueKey;
		}
		
		public Column(int columnIndex, String name, String valueKey, int width ) {
			super();
			this.columnIndex = columnIndex;
			this.name = name;
			this.valueKey = valueKey;
			this.width = width;
		}
		
	} 
}
