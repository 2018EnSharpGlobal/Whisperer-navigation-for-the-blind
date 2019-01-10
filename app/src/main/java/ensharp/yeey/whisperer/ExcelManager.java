package ensharp.yeey.whisperer;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

//Excel 관리하는 Class
public class ExcelManager {

    Context context;

    Workbook workbook;
    Sheet sheet;

    //ExcelManager 생성자
    public ExcelManager(Context _context){
        this.context = context;

        this.workbook = null;
        this.sheet = null;
    }

    //Excel에 있는 데이터 중 원하는 데이터 반환
    public String Find_Data(String param, int inspect_column, int RETURN_VALUE){

        String find_data;

        try {
            InputStream inputStream = context.getResources().getAssets().open("station_data.xls");

            workbook = Workbook.getWorkbook(inputStream);
            sheet = workbook.getSheet(Constant.EXCEL_SHEET_NUM);

            int RowEnd = sheet.getColumn(Constant.EXCEL_MAX_COLUMN - 1).length - 1;

            for(int row = Constant.EXCEL_ROW_START ; row <= RowEnd ; row++) {
                //비교 검사할 Column들의 값
                String value = sheet.getCell(inspect_column , row).getContents();

                //찾는 값이면
                if(value.equals(param)){
                    find_data = sheet.getCell(RETURN_VALUE,row).getContents();
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } finally {
            find_data = null;
            workbook.close();
        }

        return find_data;
    }

}
