package nhanhvn.data.services;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import nhanhvn.data.models.IdConversionObject;
import nhanhvn.data.models.NhanhvnProduct;
import nhanhvn.data.models.NhanhvnProducts;
import shared.persistence.DatabaseConnection;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Iterator;

public class TransactionService extends AbstractService {
	private final String PRODUCT_EXPORT_PATH = "resources/exported_product_list.csv";
	private final String PRODUCT_IMPORT_PATH = "resources/facebookid_mapping_sheet.csv";
	private DatabaseConnection databaseConnection = new DatabaseConnection();

	public String getAbsoluteImportPath() {
		File file = new File(PRODUCT_IMPORT_PATH);
		return file.getAbsolutePath();
	}

	public void exportNhanhvnProducts() throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, SQLException {
		NhanhvnProducts nhanhvnProducts = databaseConnection.getNhanhvnParentProductsFromDb();
		
		File file = new File(PRODUCT_EXPORT_PATH);
		if (!file.exists()) {
			file.createNewFile();
		} 
		
		Writer writer = Files.newBufferedWriter(Paths.get(file.getPath()));
        StatefulBeanToCsv<NhanhvnProduct> beanToCsv = new StatefulBeanToCsvBuilder<NhanhvnProduct>(writer)
                .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                .withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .build();
        
		String headerRecord = "\"idNhanh\",\"parentId\",\"productName\",\"facebookId\"\n";
		writer.write(headerRecord);
		beanToCsv.write((nhanhvnProducts.getProductList()));
		System.out.println("File duoc export o duong dan: " + file.getAbsolutePath());
		writer.close();
	}
	
    public void updateFacebookId() throws SQLException, IOException {
		File file = new File(PRODUCT_IMPORT_PATH);
		if (!file.exists()) {
			System.out.println("Khong tim thay file o :" + file.getAbsolutePath() + ", ket thuc dich vu.");
			return;
		}
        Reader reader;
        reader = Files.newBufferedReader(Paths.get(file.getPath()));

        if(reader != null) {
        	System.out.println("Reading file: " + file.getAbsolutePath());
            CsvToBean<IdConversionObject> csvToBean = new CsvToBeanBuilder<IdConversionObject>(reader)
                    .withType(IdConversionObject.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withSkipLines(1)
                    .build();
            Iterator<IdConversionObject> csvUserIterator = csvToBean.iterator();
            while (csvUserIterator.hasNext()) {
                IdConversionObject idConversionObject = csvUserIterator.next();
                databaseConnection.persistFacebookId(idConversionObject);
            }
			databaseConnection.updateFacebookIdFromProductTableToBillDetails();
        }
    }
}
