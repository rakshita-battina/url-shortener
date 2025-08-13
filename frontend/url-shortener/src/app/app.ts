import { Component,ElementRef,OnInit, ViewChild  } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { QRCodeComponent } from 'angularx-qrcode';
import { ChartConfiguration } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';



@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule,QRCodeComponent, HttpClientModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent implements OnInit {
  originalUrl: string = '';
  shortCode: string = '';
  clickCount: number | null = null;
  theme: 'light' | 'dark' = 'light';
  copied: boolean = false;  
  customCode: string = '';
  errorMessage: string = '';
  loginUsername: string = '';
  loginPassword: string = '';
  loginError: string = '';
  expiresAt: string = ''; 
  qrUrl: string = '';
  showQr = false;
  shortenedUrls: any[] = [];
  currentPage = 1;
  pageSize = 5;
  Math = Math;
  selectedFile: File | null = null;
  bulkUploadSuccess: boolean = false;
  bulkUploadedUrls: any[] = [];
  bulkCopiedStates: boolean[] = [];
  searchQuery: string = '';
  minClicks?: number;
  maxClicks?: number;
  startDate?: string;
  endDate?: string;
  filteredUrls: any[] = [];
  
chartData = {
    labels: [],
    datasets: [{
      label: 'Clicks over time',
      data: [],
      borderColor: '#007bff',
      fill: false,
      tension: 0.1
    }]
  };
loadClickChart(shortCode: string) {
  this.http.get<any[]>(`${this.baseUrl}/api/clicks/${shortCode}`).subscribe(data => {
    const grouped = data.reduce((acc, click) => {
      const date = new Date(click.timestamp).toLocaleDateString();
      acc[date] = (acc[date] || 0) + 1;
      return acc;
    }, {} as Record<string, number>);

    // this.chartData.labels = Object.keys(grouped);
    this.chartData.datasets[0].data = Object.values(grouped);
  });
}
  constructor(private http: HttpClient) {}

  baseUrl = 'http://localhost:8080'; // your Spring Boot backend

  shortenUrl(): void {
  if (!this.originalUrl) return;

  const body: any = { originalUrl: this.originalUrl };
  if (this.customCode.trim()) body.customCode = this.customCode.trim();
  if (this.expiresAt) {
    body.expiresAt = this.expiresAt;
  }
  this.http.post<{ shortCode: string }>(`${this.baseUrl}/api/shorten`, body).subscribe({
    next: res => {
      this.shortCode = res.shortCode;
      this.qrUrl = `${this.baseUrl}/r/${this.shortCode || ''}`;
      this.clickCount = null;
      this.errorMessage = '';
    },
    error: err => {
      this.errorMessage = err.error?.error || 'An error occurred.';
      this.shortCode = '';
    }
  });
  
}
toggleQr() {
    this.showQr = !this.showQr;
  }

  @ViewChild('qrWrapper', { static: false }) qrWrapper!: ElementRef;
  downloadQr(): void {
    const canvas = this.qrWrapper.nativeElement.querySelector('canvas');
    if (!canvas) return;

    const image = canvas.toDataURL('image/png');
    const link = document.createElement('a');
    link.href = image;
    link.download = `qr-${this.shortCode}.png`;
    link.click();
  }
  getAnalytics() {
  if (!this.shortCode) return;

  this.http.get<any>(`${this.baseUrl}/api/analytics/${this.shortCode}`).subscribe({
    next: (res) => {
      this.clickCount = res.clickCount; // Must match backend response JSON!
    },
    error: (err) => {
      console.error('Analytics error:', err);
      this.errorMessage = err.error?.message || 'Failed to fetch analytics';
    }
  });
}
loadUrls() {
  this.http.get<any[]>('http://localhost:8080/api/urls')
    .subscribe({
      next: (data) => {
        this.shortenedUrls = data;
      },
      error: (err) => console.error('Error loading URLs:', err)
    });
}
deleteUrl(shortCode: string): void {
  this.http.delete(`http://localhost:8080/api/url/${shortCode}`).subscribe({
    next: () => this.shortenedUrls = this.shortenedUrls.filter(u => u.shortCode !== shortCode),
    error: (err) => console.error('Delete failed', err)
  });
}
  copyToClipboard(): void {
  const url = `${this.baseUrl}/r/${this.shortCode}`;
  navigator.clipboard.writeText(url).then(() => {
    this.copied = true;
    setTimeout(() => this.copied = false, 2000); // Hide message after 2s
  });
}
    
  get paginatedUrls() {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.shortenedUrls.slice(start, start + this.pageSize);
  }

  nextPage() {
    if ((this.currentPage * this.pageSize) < this.shortenedUrls.length) this.currentPage++;
  }
  prevPage() {
    if (this.currentPage > 1) this.currentPage--;
  }
  

ngOnInit(): void {
  const saved = localStorage.getItem('theme');
  this.theme = (saved === 'dark') ? 'dark' : 'light';
  this.applyTheme();
  this.loadUrls();
}

toggleTheme(): void {
  this.theme = this.theme === 'dark' ? 'light' : 'dark';
  localStorage.setItem('theme', this.theme);
  this.applyTheme();
}

applyTheme(): void {
  const body = document.body;
  if (this.theme === 'dark') {
    body.classList.add('dark-mode');
  } else {
    body.classList.remove('dark-mode');
  }
}

onFileSelected(event: Event): void {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    this.selectedFile = target.files[0];
  }
}

uploadBulk(): void {
  if (!this.selectedFile) return;

  const formData = new FormData();
  formData.append('file', this.selectedFile);

  this.http.post<any[]>('http://localhost:8080/api/bulk-upload', formData)
    .subscribe({
      next: (data) => {
        this.bulkUploadSuccess = true;
        this.bulkUploadedUrls = data;
        this.bulkCopiedStates = Array(data.length).fill(false); // Reset copied states
        this.shortenedUrls = [...this.shortenedUrls, ...data];
        this.selectedFile = null;
        setTimeout(() => this.bulkUploadSuccess = false, 2000);
      },
      error: (err) => {
        this.bulkUploadSuccess = false;
        console.error('Bulk upload failed', err);
      }
    });
}
copyBulkToClipboard(shortCode: string, idx: number): void {
  const url = `${this.baseUrl}/r/${shortCode}`;
  navigator.clipboard.writeText(url).then(() => {
    this.bulkCopiedStates[idx] = true;
    setTimeout(() => this.bulkCopiedStates[idx] = false, 2000);
  });
}

applyFilters(): void {
  const params: any = {};
  if (this.searchQuery) params.query = this.searchQuery;
  if (this.minClicks != null) params.minClicks = this.minClicks;
  if (this.maxClicks != null) params.maxClicks = this.maxClicks;
  if (this.startDate) params.startDate = this.startDate;
  if (this.endDate) params.endDate = this.endDate;

  this.http.get<any[]>('http://localhost:8080/api/urls/search', { params })
    .subscribe(data => {
      this.filteredUrls = data;
    });
}



}
