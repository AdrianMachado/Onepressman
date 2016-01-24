#include <pebble.h>
#include <time.h>
#include <stdio.h>

AppTimer *timer;
Window *window;
TextLayer *text_layer;

char buffer[1024];
bool triggered = false;


void send(int key, int value) {
  DictionaryIterator *iter;
  app_message_outbox_begin(&iter);

  dict_write_int(iter, key, &value, sizeof(int), true);

  app_message_outbox_send();
}

void outbox_sent_handler(DictionaryIterator *iter, void *context) {
  text_layer_set_text(text_layer, "hopefully you will survive bye");
}

void outbox_failed_handler(DictionaryIterator *iter, AppMessageResult reason, void *context) {
  text_layer_set_text(text_layer, "Send failed!");
  APP_LOG(APP_LOG_LEVEL_ERROR, "Fail reason: %d", (int)reason);
}


void window_load(Window* window) {
  text_layer = text_layer_create(GRect(0, 53, 132, 168));
  text_layer_set_background_color(text_layer, GColorClear);
  text_layer_set_text_color(text_layer, GColorBlack);
  text_layer_set_text_alignment(text_layer, GTextAlignmentCenter);
  text_layer_set_font(text_layer, fonts_get_system_font(FONT_KEY_GOTHIC_18_BOLD));
  text_layer_set_text(text_layer, "Press the select button to trigger emergency");
  layer_add_child(window_get_root_layer(window), (Layer*) text_layer);
}

void down_click_handler(ClickRecognizerRef recognizer, void *context) {
  if (triggered == true) {
    text_layer_set_text(text_layer, "Emergency message cancelled.");
    app_timer_cancel(timer);
    triggered = false;
  }
}

void timer_callback(void *data) {
  send(BUTTON_ID_SELECT, 0);
  text_layer_set_text(text_layer, "Emergency message sent.");
  triggered = false;
}

void select_click_handler(ClickRecognizerRef recognizer, void *context) {
  if (triggered == false) {
    triggered = true;
    text_layer_set_text(text_layer, "Emergency message will be sent in 10 seconds. Press down to cancel.");
    timer = app_timer_register(10000, (AppTimerCallback) timer_callback, NULL);
  }
}

void click_config_provider(void *context) {
  window_single_click_subscribe(BUTTON_ID_DOWN, down_click_handler);
  window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
}

void window_unload(Window* window) {
  
}

void init() {
  window = window_create();
  window_set_window_handlers(window, (WindowHandlers) {
    .load = window_load,
    .unload = window_unload,
  });
  window_set_click_config_provider(window, click_config_provider);
  window_stack_push(window, true);
  app_message_register_outbox_sent(outbox_sent_handler);
  app_message_register_outbox_failed(outbox_failed_handler);
  app_message_open(app_message_inbox_size_maximum(), app_message_outbox_size_maximum());
  
}

void deinit() {
  tick_timer_service_unsubscribe();
  text_layer_destroy(text_layer);
  window_destroy(window);
}

int main(void) {
  init();
  app_event_loop();
  deinit();
}