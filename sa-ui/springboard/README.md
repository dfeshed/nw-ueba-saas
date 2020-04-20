Springboard
==============================================================================

 A special layout for widgets, focusing on the infinite horizontal scroll, intended to house a special set of widgets.
 
 Springboard uses `springboard-widget-lib` addon for rendering the widgets based on the [Configuration](#configuration).
 
 Note: These are high level configuration will refine the configuration along with the development
 
Usage
------------------------------------------------------------------------------
 Springboard is a routable [ember-engine](https://github.com/ember-engines/ember-engines) that is [mounted](https://github.com/ember-engines/ember-engines.com/blob/66759f39726617b3a17f1f0088ccd78ac73380ce/markdown/guide/mounting-engines.md#routable-engines) inside the sa application at `/springboard`.
 
Configuration
------------------------------------------------------------------------------
Springboard supports more than one configurations. Each configuration is a plain javascript object refer [options](#options) for more details

Example:

```

[
  {
    id: '3eff1231vvv12345',
    name: 'Analyst Springboard',
    isPredefined: true,
    period: {
      unit: 'hours',
      value: 24
    },
    owner: 'admin',
    widgets: [
      {
        columnIndex: 1,
        height: '100%',
        width: '250px',
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
      }
    ]
  }
]
```
  
### Options
- `id` - Unique identification for the spring board
    - `@type {String}`
    - Defaults to database id
- `localizionNameKey` - If specified takes the precedence over name
    - `@type {String}`    
- `name` - Name of the spring board, which is used to set the title of the page
    - `@type {String}`
    - Name given at the time of the springboard creation
    - Maximum 256 characters allowed
- `isPredefined` - Indicate out of the box (company shipped) springboard or not
    - `@type {boolean}`
    - defaults to `true`
- `period` - Time window to query for each widgets
    - `@type {object}` refer [period](#period) for details
    - Defaults to `{ value: 24, unit: 'hours'}`
    - Maximum supported is last 7 days
- `owner` - Indicates who created the springboard, this is populated while creating the springboard. For out of the box widget owner is set to `admin`
    - `@type {String}`
    - Defaults to logged in user   
- `widgets` - Indicates the layout configuration for the springboard`. More [details](#widgets)
    - `@type {array}`
    
#### period
Property to capture the time window, which are usd to query each widgets

- `unit` - indicates type 
    - `@type {String}
    - Supported values are `minutes`, `hours` and `days`
- `value` - to capture the number `minutes`, `hours` or `days`
    - `@type {number}`
    
#### widgets
Holds the configuration for widget and its position
- `columnIndex` - In which column widget has to render
    - `@type {number}`
    - Maximum allowed column values is 20
- `height` - Height for the widget
    - `@type  {String}`
    - Defaults to available screen height
-  `width` - Width for the widget
    -`@type {String}`
    - Defaults to <TBD>
- `widget` - Holds the widget configurations
    -`@type {Object}` refer [widget](#widget) for more details
            
#### widget
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
    
 
                        
