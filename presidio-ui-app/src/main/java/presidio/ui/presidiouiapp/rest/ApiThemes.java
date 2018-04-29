package presidio.ui.presidiouiapp.rest;

import fortscale.services.ThemesService;
import fortscale.utils.logging.annotation.LogException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import presidio.ui.presidiouiapp.beans.DataBean;


import java.util.Map;


@Controller
@RequestMapping("/api/themes")
public class ApiThemes {


    private ThemesService themesService;

    public ApiThemes(ThemesService themesService) {
        this.themesService = themesService;
    }

    @RequestMapping(value="/", method = RequestMethod.GET)
    @LogException
    public @ResponseBody
    DataBean<Map<String, String>> getDefaultTheme(){

        DataBean<Map<String, String>> data= new DataBean<>();
        data.setData(themesService.getDefaultTheme());
        return data;

    }



}
