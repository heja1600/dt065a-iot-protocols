import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { IMqttServiceOptions, MqttModule } from 'ngx-mqtt';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ButtonComponent } from './components/button/button.component';
import { ConnectComponent } from './views/connect/connect.component';
import { OperationsComponent } from './views/operations/operations.component';
import { PublishComponent } from './views/operations/publish/publish.component';
import { SubscribeComponent } from './views/operations/subscribe/subscribe.component';

export const MQTT_SERVICE_OPTIONS: IMqttServiceOptions = {
  connectOnCreate: false,
  protocol: 'ws',
};
@NgModule({
  declarations: [
    AppComponent,
    ConnectComponent,
    OperationsComponent,
    SubscribeComponent,
    PublishComponent,
    ButtonComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    MqttModule.forRoot(MQTT_SERVICE_OPTIONS),
  ],

  bootstrap: [AppComponent],
})
export class AppModule {}
