import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent} from './app/app';
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';


bootstrapApplication(AppComponent, {
  ...appConfig,
  providers: [
    provideCharts(withDefaultRegisterables())
  ]
}).catch((err) => console.error(err));
