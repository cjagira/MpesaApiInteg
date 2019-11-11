package ke.co.esuite.mpesa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<String> test() {
		ResponseEntity<String> res;
		
		String json = "[{\"Name\":\"Wilberforce\"},{\"Name\":\"Test\"}]";
		res = ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(json);
		return res;
	}
}
