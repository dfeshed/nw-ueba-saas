package presidio.ui.presidiouiapp.rest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ShayController {
    @GetMapping("/shay")
    public Map<String,String> greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        Map<String,String> map = new HashMap<>();
        map.put("shay","shay");
        return map;
    }
}
