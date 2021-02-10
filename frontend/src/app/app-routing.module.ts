import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConnectComponent } from './views/connect/connect.component';
import { OperationsComponent } from './views/operations/operations.component';

const appRoutes: Routes = [
  { path: 'operations', component: OperationsComponent },
  { path: 'connect', component: ConnectComponent },
  { path: '**', redirectTo: 'connect' },
];

@NgModule({
  imports: [RouterModule.forRoot(appRoutes /**, { enableTracing: true } */)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
