## Filter Engine

Basic filtering engine implementation. 

### Notes
- item ptiority is shown with color
- number of items can be set via [ITEM_COUNT](app/src/main/java/me/abhelly/filterengine/FilterFragment.java#L39) constant

### Known issues
- Text pattern excludes all "p" symbols and numbers from 1 to 4, so text "pepita 2 text" will be parsed as 3 tokens: "e", "ita", "text"
