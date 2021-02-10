import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MqttService } from 'ngx-mqtt';

@Component({
  selector: 'app-connect',
  templateUrl: './connect.component.html',
  styleUrls: ['./connect.component.css'],
})
export class ConnectComponent implements OnInit {
  connectFormGroup: FormGroup;

  constructor(
    private readonly mqttService: MqttService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.connectFormGroup = new FormGroup({
      connectUrl: new FormControl('', Validators.required),
    });
  }

  onConnect(): void {
    const connectUrl = this.connectFormGroup.get('connectUrl').value;

    try {
      this.mqttService.connect({
        url: connectUrl,
      });
      //   this.router.navigate(['/operations']);
    } catch (error) {
      console.error('failed to connect to brooker');
    }
    this.router.navigate(['/operations']); // ska inte vara här egentligen
  }
}
