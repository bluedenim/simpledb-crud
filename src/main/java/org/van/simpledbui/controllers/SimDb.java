package org.van.simpledbui.controllers;

import com.amazonaws.regions.Region;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.van.simpledbui.services.SimpleDbService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by vly on 6/21/2015.
 */
@RestController
@RequestMapping("/simdb")
public class SimDb {

    private static final Logger logger =
            Logger.getLogger(SimDb.class);

    public static class ItemAttribute {
        public String name;
        public String value;
    }

    public static class ItemData {
        public String itemName;
        public ItemAttribute attributes[];

    }

    @Autowired
    private SimpleDbService dbService;

    @RequestMapping("/{region}/dbs")
    public Object listDbs(@PathVariable String region) {
        logger.debug(String.format("listing domains for region %s", region));
        SimpleDbService.PaginatedResult<List<String>> result =
                dbService.listDomains(dbService.findRegion(region));
        return result;
    }

    @RequestMapping(value ="/{region}/dbs/{db}", method = {RequestMethod.POST})
    public Object createDb(@PathVariable String region, @PathVariable String db)
    {
        logger.debug(String.format("Creating domain %s in region %s", db, region));
        if (!StringUtils.isEmpty(db)) {
            dbService.createDomain(dbService.findRegion(region), db);
        }
        Map<String,String> model = new HashMap<>();
        model.put("status", "OK");
        return model;
    }

    @RequestMapping(value ="/{region}/dbs/{db}", method = {RequestMethod.DELETE})
    public Object deleteDb(@PathVariable String region, @PathVariable String db)
    {
        logger.debug(String.format("Deleting domain %s in region %s", db, region));
        if (!StringUtils.isEmpty(db)) {
            dbService.deleteDomain(dbService.findRegion(region), db);
        }
        Map<String,String> model = new HashMap<>();
        model.put("status", "OK");
        return model;
    }

    @RequestMapping("/{region}/{db}/query")
    public Object query(@PathVariable String region, @PathVariable String db, @RequestParam String query,
                        @RequestParam(required = false) String nextToken) {
        query = query.trim();
        logger.debug(String.format("%s:%s gets query %s, nextToken %s", region, db, query, nextToken));
        Region regionObj = dbService.findRegion(region);
        return dbService.select(regionObj, db, query, nextToken);
    }

    @RequestMapping(value = "/{region}/{db}/item/{item}", method = {RequestMethod.POST})
    public Object editAttribute(@PathVariable String region, @PathVariable String db, @PathVariable String item,
                                @RequestParam String attribute,
                                @RequestParam String value) {
        Map<String,String> model = new HashMap<>();
        logger.debug(String.format("%s:%s:%s %s -> %s", region, db, item, attribute, value));
        Region regionObj = dbService.findRegion(region);
        dbService.updateAttribute(regionObj, db, item, attribute, value);
        model.put("status", "OK");
        return model;
    }

    @RequestMapping(value = "/{region}/{db}/items", method = {RequestMethod.POST})
    public Object addItem(@PathVariable String region, @PathVariable String db,
                          @RequestBody ItemData itemData) {
        Map<String,String> model = new HashMap<>();
        if (!StringUtils.isEmpty(itemData)) {
            dbService.addItem(dbService.findRegion(region), db, itemData);
            model.put("status", "OK");
        } else {
            throw new IllegalArgumentWebException("Empty item name");
        }
        return model;
    }

    @RequestMapping(value = "/{region}/{db}/deletion/item", method = {RequestMethod.POST})
    public Object deleteItem(@PathVariable String region, @PathVariable String db, @RequestParam String itemName) {
        Map<String,String> model = new HashMap<>();
        logger.debug(String.format("Delete item %s:%s:%s", region, db, itemName));
        Region regionObj = dbService.findRegion(region);
        dbService.deleteItem(regionObj, db, itemName);
        model.put("status", "OK");
        return model;
    }

    @RequestMapping(value = "/{region}/{db}/deletion/item/attribute", method = {RequestMethod.POST})
    public Object deleteItemAttribute(@PathVariable String region, @PathVariable String db, @RequestParam String itemName,
        @RequestParam String attrName) {
        Map<String,String> model = new HashMap<>();
        logger.debug(String.format("Delete attribute %s:%s:%s:%s", region, db, itemName, attrName));
        Region regionObj = dbService.findRegion(region);
        dbService.deleteAttribute(regionObj, db, itemName, attrName);
        model.put("status", "OK");
        return model;
    }
}
