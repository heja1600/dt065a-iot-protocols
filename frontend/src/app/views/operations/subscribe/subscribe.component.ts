import { Component, OnInit } from '@angular/core';
import { MqttService } from 'ngx-mqtt';

@Component({
  selector: 'app-subscribe',
  templateUrl: './subscribe.component.html',
  styleUrls: ['./subscribe.component.css'],
})
export class SubscribeComponent implements OnInit {
  constructor(private readonly mqttService: MqttService) {}

  ngOnInit(): void {}

  subscribeToTopic(topic: string): void {
    this.mqttService.observe('temperature').subscribe((value) => {
      console.log({ value });
      /** print to text area */
    });
  }
}
