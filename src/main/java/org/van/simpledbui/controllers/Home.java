/**
 * 
 */
package org.van.simpledbui.controllers;

import com.amazonaws.regions.Region;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.van.aws.AwsClientBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author vly
 *
 */
@Controller
@RequestMapping("/home")
public class Home {

    private final AtomicReference<List<String>> regionNames = new AtomicReference<>();

	@RequestMapping("/index")
	public String index(Model model) {
		model.addAttribute("regions", getRegionNames());
		return "index";
	}

    List<String> getRegionNames() {
        List<String> names = regionNames.get();
        if (null == names) {
            names = new LinkedList<>();
            for (Region r : AwsClientBuilder.getRegions()) {
                names.add(r.getName());
            }
            regionNames.set(names);
        }
        return names;
    }

}
