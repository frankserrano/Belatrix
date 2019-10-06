package com.belatrix;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.belatrix.dto.ProductDto;
import com.belatrix.utils.Email;

public class EbayTest {

	private Properties prop;
	private WebDriver driver;

	private void loadProperties() throws FileNotFoundException, IOException {
		prop= new Properties();
		prop.load(new FileReader("./src/test/resources/dev-config.properties"));
		System.setProperty("webdriver.chrome.driver", "./src/test/resources/driver/web/chrome/chromedriver.exe");
	}

	@BeforeTest
	public void setUp() throws FileNotFoundException, IOException {
		this.loadProperties();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		// 1 Enter ebay
		driver.get("https://www.ebay.com");
	}

	@Test
	public void search() throws InterruptedException {
		// 2 Search for shoes
		WebElement searchText = driver.findElement(By.id(prop.getProperty("searchText")));
		searchText.sendKeys("shoes");
		searchText.sendKeys(Keys.ENTER);
		Thread.sleep(2000);

		this.filterByBrand();
		this.filterBySize();
		this.orderByPrice();
		this.selectItems();
		this.sendEmail();
	}

	private void filterByBrand() throws InterruptedException {
		// 3 Select brand PUMA
		WebElement checkboxBrand = driver.findElement(By.xpath(prop.getProperty("checkboxBrand")));
		checkboxBrand.click();
		Thread.sleep(2000);
	}

	private void filterBySize() throws InterruptedException {
		// 4 Select size 10
		WebElement checkboxSize = driver.findElement(By.xpath(prop.getProperty("checkboxSize")));
		checkboxSize.click();
		// 5 Print the number of results
		System.out.println(driver.findElement(By.xpath(prop.getProperty("numberOfResults"))).getText());
	}

	private void orderByPrice() throws InterruptedException {
		// 6 Order by price ascendant
		WebDriverWait buttonWait = new WebDriverWait(driver, 20);
		WebElement buttonOrderByPrice = buttonWait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath(prop.getProperty("buttonOrderByPrice"))));
		buttonOrderByPrice.click();
		buttonOrderByPrice.click();
		driver.findElement(By.xpath(prop.getProperty("priceAscendant"))).click();
		Thread.sleep(2000);
	}

	private void selectItems() {
		List<ProductDto> productListAsc = new ArrayList<ProductDto>();
		for (int i = 1; i < 6; i++) {
			WebElement itemPrice = driver
					.findElement(By.xpath("//li[@data-view='mi:1686|iid:" + i + "']//span[@class='s-item__price']"));
			WebElement itemName = driver.findElement(By.xpath("//li[@data-view='mi:1686|iid:" + i + "']//h3"));
			ProductDto productDto = new ProductDto();
			productDto.setName(itemName.getText());
			productDto.setPrice(Double.parseDouble(itemPrice.getText().replace("S/. ", "")));
			productListAsc.add(productDto);
			// 8 Take the first 5 products with their prices and print them in console.
			System.out.println(itemPrice.getText());
		}
		// 7 Assert the order taking the first 5 results
		Assert.assertTrue(productListAsc.size() == 5);

		List<ProductDto> productListDesc = new ArrayList<ProductDto>();
		for (int i = productListAsc.size() - 1; i >= 0; i--) {
			ProductDto productDto = new ProductDto();
			productDto.setName(productListAsc.get(i).getName());
			productDto.setPrice(productListAsc.get(i).getPrice());
			productListDesc.add(productDto);
		}

		Collections.sort(productListAsc, new Comparator<ProductDto>() {
			public int compare(ProductDto obj1, ProductDto obj2) {
				return obj1.getName().compareTo(obj2.getName());
			}
		});

		// 9 Order and print the products by name
		System.out.println("---------List Order By Name---------");
		for (ProductDto productDto : productListAsc) {
			System.out.println(productDto.getName());
			System.out.println(productDto.getPrice());
		}

		// 10 Order and print the products by price in descendant mode
		System.out.println("---------List Order By Price Desc---------");
		for (ProductDto productDto : productListDesc) {
			System.out.println(productDto.getName());
			System.out.println(productDto.getPrice());
		}

	}

	private void sendEmail() {
		Email email = new Email();
		email.sendEmail(prop);

	}

	@AfterTest
	public void teardown() {
		driver.quit();
	}

}
