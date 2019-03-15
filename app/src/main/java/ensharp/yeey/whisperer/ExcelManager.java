package ensharp.yeey.whisperer;

import android.content.Context;
import android.util.Log;

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
    public ExcelManager(){
        this.workbook = null;
        this.sheet = null;
    }

    //Context 설정
    public void setContext(Context _context){
        this.context = _context;
    }

    //Excel에 있는 데이터 중 원하는 데이터 반환
    public String Find_Data(String param, int inspect_column, int RETURN_VALUE){

        String find_data = null;

        try {
            InputStream inputStream = context.getResources().getAssets().open("station_data.xls");
            Log.e("4","4");
            workbook = Workbook.getWorkbook(inputStream);
            Log.e("11","11");
            sheet = workbook.getSheet(Constant.EXCEL_SHEET_NUM);
            Log.e("5","5");
            int RowEnd = sheet.getColumn(Constant.EXCEL_MAX_COLUMN - 1).length - 1;
            Log.e("6","6");
            for(int row = Constant.EXCEL_ROW_START ; row <= RowEnd ; row++) {
                //비교 검사할 Column들의 값
                String value = sheet.getCell(inspect_column , row).getContents();
                Log.e("value",value);
                //찾는 값이면
                if(value.equals(param)){
                    find_data = sheet.getCell(RETURN_VALUE,row).getContents();
                    break;
                }

            }
        } catch (Exception e){
            Log.e("Error",e.toString());
            e.printStackTrace();
        }
//        catch (IOException e) {
//            e.printStackTrace();
//            Log.e("Error",e.toString());
//        } catch (BiffException e) {
//            e.printStackTrace();
//            Log.e("Error",e.toString());
//        }
        finally {
            workbook.close();
        }

        return find_data;
    }

}
