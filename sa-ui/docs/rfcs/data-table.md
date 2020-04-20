# Summary

The `rsa-data-table` component is a multi-column non-crosstabbed table for displaying a potentially large array of data records. It supports:

* declarative and imperative column declaration
* an optional row of column headers
* lazy rendering of data records on scroll
* resizable column widths
* draggable column headers

# How To Use

The `rsa-data-table` component must have a child `rsa-data-table/body` component in order to render the data records. It is responsible for rendering the scrollable area that displays data values.

`rsa-data-table` can also have an optional child component `rsa-data-table/header` if column headers are desired; otherwise, the `rsa-data-table/header` component can be omitted.

The consumer of `rsa-data-table` should specify which columns of data should be displayed. This can be done either declaratively or imperatively.

## The Declarative Approach

Using a declarative approach means that the consumer specifies the cells as markup inside the `rsa-data-table` markup.  Specifically, cells can be embedded inside the `rsa-data-table/body` section and, if desired, in the optional `rsa-data-table/header` section as well.

```hbs
{{#rsa-data-table items=...}}

  {{!header section: optional}}
  {{#rsa-data-table/header}}
    ...{{!header cells go here}}..
  {{/rsa-data-table}}

  {{!body section: required}}
  {{#rsa-data-table/body}}
  ...{{!body cells go here}}...
  {{/rsa-data-table/body}}

{{/rsa-data-table}}
```

The benefit of this approach is that the consumer has explicit control over the markup.  The disadvantage is that the columns are fixed in the markup and cannot be modified (removed, added, re-ordered) ad-hoc at run-time.

### Basic Declarative Columns

When specifying columns declaratively, the consumer of `rsa-data-table` includes markup inside the `{{#rsa-data-table/body}}` block. This markup will be repeated once for each data record.  The data record will be yielded by `{{#rsa-data-table/body}}` as its first block parameter. It's index will be yielded as the 2nd block parameter.

The content inside `{{#rsa-data-table/body}}` should define the inner contents of a single row.  It should include a `rsa-data-table/body-cell` for every column to be shown.  Note that there's no need to wrap the `{{#rsa-data-table/body-cell}}`s in a `{{#rsa-data-table/body-row}}`. That will already be done automatically by the `rsa-data-table/body` template.

```hbs
{{#rsa-data-table items=..}}

  {{#rsa-data-table/body as |item index|}}

    {{#rsa-data-table/body-cell}}
      {{index}}: {{item.id}} - {{item.name}}
    {{/rsa-data-table/body-cell}}

    {{#rsa-data-table/body-cell}}
      {{my-date-time-component value=item.created}}
    {{/rsa-data-table/body-cell}}

  {{/rsa-data-table/body}}

{{~/rsa-data-table}}
```

### Setting Cell Widths

In the declarative approach, the width of each cell can be set using the `body-cell` component's `width` attribute. Units are supported but optional (default is `px`). If units are specified, ensure that the same units are used for all the cells.

```hbs
{{#rsa-data-table items=..}}

  {{#rsa-data-table/body as |item index|}}

    {{#rsa-data-table/body-cell width="66%"}}
      {{index}}: {{item.id}} - {{item.name}}
    {{/rsa-data-table/body-cell}}

    {{#rsa-data-table/body-cell width="34%"}}
      {{my-date-time-component value=item.created}}
    {{/rsa-data-table/body-cell}}

  {{/rsa-data-table/body}}

{{~/rsa-data-table}}
```

### Using Custom Cell Components

If the consumer wishes to use a different cell component instead of `rsa-data-table/body-cell`, they can. Simply include the custom component class in place of the `rsa-data-table/body-cell` components inside the `rsa-data-table/body` block above. However, that component class should apply the mixin `rsa-data-table/mixins/is-cell` and CSS class `rsa-data-table-body-cell` in order for it to be positioned properly. (The mixin & CSS class are used by the `rsa-data-table/body-cell` component.)

### Using Custom Row Components

If the consumer wishes to use a different row component instead of `rsa-data-table/body-row`, they can. Simply set the `rowComponentClass` attribute of `rsa-data-table/body` to the name of the custom component. Note that the component must be responsible for positioning its DOM vertically. Each instance of the component will be assigned the following attributes at run-time:

  * `item`: (object) The data record corresponding to that row.

  * `index`: (number) The index of the data record corresponding to that row.

  * `top`: (number) The y-coordinate (in pixels) at which the row should be positioned, relative to the root element of `rsa-data-table/body`.

As a helpful aid, the mixin `rsa-data-table/mixins/is-row` and the CSS class `rsa-data-table-body-row` can be applied to a custom row component. Together, this mixin & CSS class will place the row DOM automagically using absolute positioning. (It is used by the `rsa-data-table/body-row` component.)

### Declarative Column Headers

To add column headers declaratively, simply add a `rsa-data-table/header` block with `rsa-data-table/header-cell`s for each column.  Take care to ensure that the order and widths of the `header-cell`s match those of the `body-cell`s.

```hbs
{{#rsa-data-table items=..}}

  {{#rsa-data-table/header}}

    {{#rsa-data-table/header-cell width="66%"}}
      ID: Name
    {{/rsa-data-table/header-cell}}

    {{#rsa-data-table/header-cell width="34%"}}
      Created Date
    {{/rsa-data-table/header-cell}}

  {{/rsa-data-table/header}}

  {{#rsa-data-table/body as |item index|}}

    {{#rsa-data-table/body-cell width="66%"}}
      {{item.id}}: {{item.name}}
    {{/rsa-data-table/body-cell}}

    {{#rsa-data-table/body-cell width="34%"}}
      {{my-date-time-component value=item.created}}
    {{/rsa-data-table/body-cell}}

  {{/rsa-data-table/body}}

{{~/rsa-data-table}}
```

## The Imperative Approach

In the imperative approach, the consumer specifies columns by providing `rsa-data-table` with an array of column information via the `columnsConfig` attribute.  This list can include information about each column, such as the corresponding record field, an optional title, optional column width & even the name of an Ember Component class to use for rendering the column DOM.  This approach is different from the declarative approach, which does not use the table's `columnsConfig` attribute, and instead uses nested `body-cell` components to specify the columns.  

In the imperative approach, the information in `columnsConfig` is used to compute an array of "column models", which can then be accessed via the table's `columns` attribute.  These models are handed down to the table's children components and used in their template bindings, so that their display reflects the current info in `columns`.

The benefit of the imperative approach is that columns can be manipulated (removed, added, re-ordered, resized) dynamically at run-time.  To manipulate the columns, we simply manipulate the `columns` array. Reordering the `columns` array causes the display of the cells to be re-ordered. Adding & removing entries to & from the `columns` array causes cells to be added to & removed from DOM respectively.

The disadvantage of the imperative approach is that, since the markup is driven programmatically by the list of columns, the consumer cannot explicitly dictate all the markup like they can in the declarative approach.  However, there is still some room for customization, as described in the sections below.

### Basic Imperative Columns

Unlike the declarative approach, when using the imperative approach the consumer cannot explicitly embed an individual `rsa-data-table/body-cell` in the `rsa-data-table/body` block for each column.  This is because the number and order of columns is determined dynamically by the table's `columns` attribute.

However, the data table still allows for some custom markup when using the imperative approach.  Rather than embedding multiple `body-cell`s, the consumer may embed a **single** `body-cell` which will automatically be used for each column:

```hbs
{{#rsa-data-table items=.. columnsConfig=..}}

  {{#rsa-data-table/body as |item index column|~}}

    {{#rsa-data-table/body-cell item=item index=index column=column}}
      {{!cell contents goes here}}
    {{/rsa-data-table/body-cell}}

  {{/rsa-data-table/body}}

{{~/rsa-data-table}}
```

As illustrated above, the `body-cell` content should be generic enough to handle the display of any column for this table at run-time.  This is do-able because the `{{#rsa-data-table/body}}` block will yield a 3rd parameter called `column`.  This `column` parameter, known as the "column model", is an Ember Object that represents the column which is currently being rendered.  It is a member of the table's `columns` array, which in turn is computed from the table's `columnsConfig`.

### Customizing Body Cell Content

Note that the `body-cell` component does not require markup inside of its block.  By default, if it does not have a block, `body-cell` will simply try to render the corresponding field `column.field` of the data record `item`.  In other words, the following are equivalent:

```hbs
{{!These two cells below render equivalent output:}}

{{rsa-data-table/body-cell item=item index=index column=column}}

{{#rsa-data-table/body-cell item=item index=index column=column}}
  {{~get item column.field~}}
{{/rsa-data-table}}
```

However, there are many cases when the consumer may wish to overwrite the default display and render a custom display instead.  For example, to continue with the example from the declarative approach, we could use the imperative approach as follows:

```hbs
{{#rsa-data-table items=.. columnsConfig="name,created"}}

  {{#rsa-data-table/body as |item index column|~}}
    {{#rsa-data-table/body-cell}}

      {{#if (is-equal column.field "name")}}
        {{!for the name column, include index & id as well}}
        {{index}}: {{item.id}} - {{item.name}}
      {{else if (is-equal column.field "created")}}
        {{!for the created date column, use some custom component}} {{my-date-time-component value=item.created}}
      {{else}}  
        {{!for any other columns, show the field value}}
        {{get item column.field}}
      {{/if}}

    {{/rsa-data-table/body-cell}}
  {{/rsa-data-table/body}}

{{~/rsa-data-table}}
```

### Using Custom Body Cell Components

In the previous section, we described how to customize the content inside of an `{{#rsa-data-table/body-cell}}`. But to take customization even further, the consumer could choose to use a different cell component entirely.  This could be done using a variety of standard templating techniques, such as using `{{#if}}` or `{{#component}}` helpers.  The arguments passed into such helpers could include `item` & `index` as well as the `column` model, if desired.  For example, suppose each `column` model was assigned a `componentClass` property by the consumer.  Then the template could read this property to determine the appropriate component to use for rendering that column, as in this example below:

JavaScript:
```js
let myColumnsConfig = [{
  field: 'name'
}, {
  field: 'created',
  componentClass: 'my-date-time-cell-component'
}];
```

Template:
```hbs

{{#rsa-data-table items=myItems columnsConfig=myColumnsConfig}}

  {{#rsa-data-table/body as |item index column|~}}

    {{component (if column.componentClass column.componentClass 'rsa-data-table/body-cell')
      item=item
      index=index
      column=column}}

  {{/rsa-data-table/body}}

{{~/rsa-data-table}}
```

Alternatively, the consumer could choose to base their choice of cell component on the data type of a column. In such an approach, the consumer could supply a data type for each column rather than a componentClass, as shown below:

JavaScript:
```js
let myColumnsConfig = [{
  field: 'name',
  dataType: 'text'
}, {
  field: 'created',
  dataType: 'date-time'
}];
```

Template:
```hbs
{{#rsa-data-table items=myItems columnsConfig=myColumnsConfig}}

  {{#rsa-data-table/body as |item index column|~}}

    {{component (concat 'my-' (if column.dataType column.dataType 'default') '-cell-component')
      item=item
      index=index
      column=column}}

  {{/rsa-data-table/body}}

{{~/rsa-data-table}}
```

Either of these approaches, or even a hybrid of the two, works with `rsa-data-table` because the customization logic is driven by the consumer, not by `rsa-data-table`. The `rsa-data-table` component remains agnostic to the logic for choosing the cell; it simply yields information to the consumer.

### About `columnsConfig`

Note that the examples in the previous two sections used two formats for `columnsConfig`: a String format, and an Array format. These different formats are supported for convenience.  The String format is simple and easy to use in a template (without requiring some JavaScript variable to back it up), while the Array format offers more flexibility.  

Here are the different formats supported for `columnsConfig`:

  1. **A comma-delimited String** of fields.

      Example: `'name,created:Created Date,desc:Description'`

      Each comma-delimited value should be the name of a property in the `items` array. In this format, if column headers will be displayed (by including a `{{rsa-data-table/header}}` block inside `{{#rsa-data-table}}`), you can include column titles here by following each field name with a colon (`:`) and title (e.g., `name,created:Created Date,desc:Description`).

  2. **An Array of Strings**.

      Example: `['name', 'created:Created Date', 'desc:Description']`

      Similar to (1) above, but the string values are in an array rather than delimited by commas.

  3. **An Array of Objects**.  

      Example 1: `[{ field: 'name'}, { field: 'created', title: 'Created Date'},{ field: 'desc', title: 'Description '}]`

      Example 2: `[ Ember.Object.create({ field: 'name' }), Ember.Object.create({ .. }), .. ]`

      Each object can be either a POJO or an Ember.Object. Each object represents the config for a column to be displayed, with the following properties:

      - `field`: The name of the JSON field from which to read the display value. Required.

      - `title`: Optional string to display in the column title. Only used if `{{rsa-data-table/header}}` is included in the `{{#rsa-data-table}}` block).

      - `width`: Optional default width for this column. Can be a number (pixels) or a string with CSS units (e.g., `50%`). If missing, a default width will be applied.

      Each object will be converted to an Ember.Object (if it isn't already) and added to the table's `columns` array to serve as a "column model".

      For customization, additional properties can be set on the object (such as `dataType` & `componentClass` in the examples from the previous section above).  These additional properties will be harmlessly ignored by `rsa-data-table` but will still be included in the column models that get passed down to the table cells. Therefore the consumer can leverage them in customization logic.

### Imperative Column Headers

Similar to the declarative approach, in the imperative approach column headers can be added to `rsa-data-table` by including an `rsa-data-table/header` child component in the `{{#rsa-data-table}}` block:

```hbs
{{#rsa-data-table items=myItems columnsConfig=myColumnsConfig}}

  {{rsa-data-table/header}}

  {{#rsa-data-table/body as |item index column|~}}

    {{! ... }}

  {{/rsa-data-table/body}}

{{~/rsa-data-table}}
```

Note that no content is required inside the `rsa-data-table/header` block. It will automatically detect that columns have been specified imperatively, and it will render an `rsa-data-table/header-cell` component for each column in the table's `columns`.

However, the customer can choose the customize the header cells' contents, if desired.  This is done inside the `rsa-data-table/header` block.  However, in the imperative approach, the consumer cannot specify an individual cell for each column, because the number & order of the columns is dynamically data-driven. Instead, the consumer may specify the inner contents **single** header cell which will act as a template for each of the header cells' contents:

```hbs
{{#rsa-data-table items=myItems columnsConfig=myColumnsConfig}}

  {{#rsa-data-table/header as |column index|}}

    {{!content inside this header block will be wrapped in an `rsa-data-table/header-cell` component for each column in `columns`}}
    <h2>Column {{index}}</h2>
    <h3>{{if column.title column.title column.field}}</h3>

  {{/rsa-data-table/header}}

  {{#rsa-data-table/body as |item index column|~}}

    {{! ... }}

  {{/rsa-data-table/body}}

{{~/rsa-data-table}}
```

As indicated above, the `{{#rsa-data-table/header}}` block will iterate over each column in `columns`, yielding the column and its index for the consumer to use.

### Resizing Columns With Drag-Drop

In the imperative approach, column widths can be resized via drag-drop. This is thanks to the fact that each column has a corresponding model which can store the column's width. A change to the model's width will be applied to all cells in that column simultaneously because every cell in a column has a template binding to the same column model object.

To resize a column, the user can drag a column's "size handle".  By default, size handles will be included in the left & right edges of the header cell for each column, except for the left edge of the very first column.  They are implemented as `rsa-data-table-cell-resizer` components.  They are automatically included in the `rsa-data-table-header-cell` template when you use the imperative approach.  Note that if the consumer chooses to customize the contents of the header cells, the size handles will still be included in addition to whatever custom content the consumer provides.

Column resizing can be enabled & disabled by setting the boolean property `enableResizeColumn` of the `rsa-data-table` component.

When `enableResizeColumn` is true, the optional property `onResizeColumn` can be used to customize the resizing behavior.  By default, if `onResizeColumn` is not defined, the data table will apply the resize by updating the column's model with a new `width` value.  However, if `onResizeColumn` is defined, then that callback will be invoked.  If it returns truthy, the data table will update the column model's `width` as usual; but if it returns falsey, then the data table will do nothing.  This allows `onResizeColumn` to customize the logic for whether a resize should be applied and/or how it should be applied.  `onResizeColumn` will be invoked with 2 arguments:  the column model object (as yet unchanged), and the newly requested width (in pixels).

### Re-Ordering Columns With Drag-Drop

In the imperative approach, columns can be reordered dynamically.  The order of the columns is driven by the order of the column models in the table's `columns` attribute.  To change the order of the columns displayed, we merely rearrange the order of the models in `columns`.

Column reordering can be enabled & disabled by setting the boolean property `enableReorderColumns` of the `rsa-data-table` component.

When `enableReorderColumns` is true, the optional property `onReorderColumns` can be used to customize the reorder behavior.  By default, if `onReorderColumns` is not defined, the data table will apply the reorder by re-sorting the table's `columns` array.  However, if `onReorderColumns` is defined, then that callback will be invoked.  If it returns truthy, the data table will update the `columns` array as usual; but if it returns falsey, then the data table will do nothing.  This allows `onReorderColumns` to customize the logic for whether a reorder should be applied and/or how it should be applied.  `onReorderColumns` will be invoked with the following arguments:

* the current `columns` array (unchanged);

* the `columns` array in its newly requested order;

* the `columns` member whose position within `columns` is being moved;

* the original index of the column being moved;

* the newly requested index of the column being moved.

To reorder columns, the user can drag a column's "move handle".  If the consumer chooses to customize the header cell's contents, a move handle will NOT be included automatically.  The consumer is responsible for including an (optional) move handle in their custom markup.  

Any DOM element(s) can serve as a move handle.  Simply assign the DOM element(s) a CSS class `"js-move-handle"`. To continue with our previous example, here's how we could make the custom headers act as move handles:

```hbs
{{#rsa-data-table items=myItems columnsConfig=myColumnsConfig}}

  {{#rsa-data-table/header as |column index|}}

    <h2 class="js-move-handle">Column {{index}}</h2>
    <h3 class="js-move-handle">{{if column.title column.title column.field}}</h3>

  {{/rsa-data-table/header}}

  {{! ... }}

{{~/rsa-data-table}}
```

Each `header-cell` component will attempt to find the move handle(s) via a DOM querySelector.  If the consumer wishes to use some other querySelectors besides `.js-move-handle'`, they can specify custom selectors using the `moveHandleSelector` attribute of the `rsa-data-table/header` component:

```hbs
{{#rsa-data-table items=myItems columnsConfig=myColumnsConfig}}

  {{#rsa-data-table/header moveHandleSelector=".foo" as |column index|}}

    <h2 class="foo">Column {{index}}</h2>
    <h3 class="foo">{{if column.title column.title column.field}}</h3>

  {{/rsa-data-table/header}}

  {{! ... }}

{{~/rsa-data-table}}
```

## Lazy Rendering

This implementation of `rsa-data-table` implements "lazy rendering"; that is, the rendering of records in DOM as the user scrolls to those records. The records which are not visible within the scrolling viewport are not included in the DOM.  

The benefit of lazy rendering is performance.  Generally speaking, more DOM equals more memory usage and incurs a performance cost in the browser.  While rendering a list of 10 or even 100 records may not require much DOM, and therefore may not create a noticeable performance degradation, render a list of 1,000 or 10,000 records can have a perceiveable toll on perceived performance.  Therefore, lazy rendering is recommended for large volumes of data.

By default, lazy rendering is disabled in `rsa-data-table`.  To enable lazy rendering, simply set the `lazy` attribute to `true`.  This is supported for both the declarative & imperative approaches described in this document.

```hbs
{{#rsa-data-table lazy=true ..}}
  ..
{{/rsa-data-table}}
```

## About Row Clicking

This implementation of `rsa-data-table` supports an optional property called `onRowClick`.  This configurable callback will be invoked when the user clicks on a row in the table's body section (but not header section).  This callback can be used to customize what action occurs as a result of user clicking (e.g., navigating to another route in your app).  The callback will be invoked with the following arguments:

* the data record from the `items` data array that corresponds to the clicked row;

* the index of the data records;

* the click DOM event, in a jQuery wrapper.


## About Sorting

This implementation of `rsa-data-table` is agnostic to sorting.  That is, it does not include any sorting functionality but it does not exclude the possiblity of adding such functionality as a customization.

For example, suppose the consumer wanted to make the columns sort when the user clicks on a column title.  This can implemented with a little customization; specifically:

1. The consumer would provide custom content inside `{{#rsa-data-table/header-cell}}` that fires a custom sort action when clicked.

2. The sort action would then responsible for applying the sort to the data Array.

3. The table would then update its display thanks to its `items` template binding.

This approach allows for sorting to be done either on the server tier or on the client tier.
