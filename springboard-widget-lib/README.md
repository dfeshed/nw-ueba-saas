Springboard Widget Library
==============================================================================

 A special widgets library for springboard.
 
 Note: These are high level configuration will refine the configuration along with the development
 
Usage
------------------------------------------------------------------------------
 Springboard is a addon 
 
Configuration
------------------------------------------------------------------------------
Widget will take configuration for addon [options](#options) for more details

Note : will be refining the options along with the development
Example:

```

widget: {
  id: '1232ssdd05gt5h6',
  name: 'Top Risky Hosts',
  leadType: 'Hosts',
  widgetType: 'TOP_N_LEADS',
  widgetParams: {
    columns: ['hostName', 'riskScore', 'osType'],
    sort: {
      keys: ['score'],
      descending: true
    },
    aggregate: {
      column: ['osType'],
      type: 'COUNT',
      chartType: 'donut'
    }
  },
  query: {
    criteria: {
      criteriaList: [
        {
          expressionList: [
            {
              restrictionType: 'EQUAL',
              propertyName: 'osType',
              propertyValues: [
                {
                  value: false
                }
              ]
            }
          ],
          predicateType: 'AND'
        }
      ],
      predicateType: 'AND'
    }
  }
}

```
  
### Options
Configuration for the widget, including datasource to query, sort field, sort order, columns etc ...
- `id` - unique identification for the widget
    - `@type {String}` id
    - Defaults to database value 
- `localizionNameKey` - If specified takes the precedence over name
    - `@type {String}`    
- `name` - Name of the widget
    - `@type {String}`
    - Maximum 256 character allowed
- `leadType` - Indicates widget belongs to which lead (alerts, hosts, files, events, incidents)
    - `@type {String}`
- `widgetType` - Type of the widget to render. It will support only available widgets in `widget-lib`
    - `@type {String}`    
- `params` - Additional parameters for the widget, Note: we are giving it as hash, params might be different from widget to widget
Here once can specify columns to display, default sort and order, visualization, aggregation column etc
    - `@type {Object}`
- `period` - Time window to query, if specified takes the precedence
    - `@type {object}` refer [period](#period) for details
    - Defaults to `{ value: 24, unit: 'hours'}`
    - Maximum supported is last 7 days
- `dataSource` - Query against source, (service id). If not specified it fall backs to default data source set by the user
    - `@type {String}`
- `query` - holds valid mongo or core device search criteria excluding
    - `@type {String}`
    - For mongodb you need pass the query as searchRequest and for core service need to pass it differently
- `size` - Size of the result set returned
    - `@type {Number}`
    - Defaults to `25`
- `isPredefined` - Indicate out of the box widget or not
    - `@type {Boolean}`
    - Defaults to `false`
- `owner` - Owner of the widget
    - `@type {String}`
    - Populated with logged in user

#### widgetParams
 For TOP_N_LEADS following widgetParams are supported
 
- `columns` -  Array of columns configuration, which are going to display
- `sort` - Sorting configuration for leads
- `aggregate` - Aggregate configuration for leads
    - `column` - aggregate column name
    - `type` - Type of aggregation
    -  `widget` - Widget to display aggregate data
        - `type` - type of the widget/ component name
        - `options`- widget options
    
 
                        
    
#### period
Property to capture the time window, which are usd to query each widgets

- `unit` - indicates type 
    - `@type {String}
    - Supported values are `minutes`, `hours` and `days`
- `value` - to capture the number `minutes`, `hours` or `days`
    - `@type {number}`