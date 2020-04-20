rsa-dashboard
==============================================================================
``rsa-dashboard`` addon for displaying the overview information of the module

Usage
------------------------------------------------------------------------------
````
{{rsa-dashboard columns=2 dashletConfig=dashletConfig}}
````

Configuration
------------------------------------------------------------------------------
Configuration for the rsa-dashboard

### Component Configuration
#### columns
Columns property indicates the layout of the rsa-dashboard, Ex: 2 column layout or 3 column layout.

| Name | Type | Description |
| ---- | ---- | ---------|
| columns | `number` | Indicates the layout of the rsa-dashboard |

#### Dashlet configuration 
Configuration for individual dashlet which are to be displayed in the rsa-dashboard layout

| Name | Type | Description |
| ---- | ---- | ---------|
| dashletConfig | `array` | Array of dahslet configuration |

##### dashletConfig
| Name | Type | Description |
| ---- | ---- | ---------|
| title | `String` | title of the dashlet |
| columnIndex | `number` | indicate in which column dashlet should render |
| componentClass | `String` | Path of the component class |
| autoRefresh | `Boolean` | Default to false. True to refresh the content on regular interval |



Dashlet Developer
------------------------------------------------------------------------------

- Create the dashlet related code inside the name-space, for ex: All endpoint related dashlet code go inside ``endpoint`` folder
- Individual dashlet should responsible for getting the data from the API
- Redux state should also name-spaced


 
