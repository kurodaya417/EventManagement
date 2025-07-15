// Event Management System - Frontend Application
class EventManagementApp {
    constructor() {
        this.apiBase = '/api';
        this.currentView = 'dashboard';
        this.currentPage = 0;
        this.pageSize = 10;
        this.currentEventId = null;
        this.events = [];
        this.statistics = {};
        
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.showView('dashboard');
        this.loadDashboard();
    }

    setupEventListeners() {
        // Navigation
        document.querySelectorAll('.nav-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                this.switchView(e.target.dataset.view);
            });
        });

        // Search and filters
        document.getElementById('search-btn').addEventListener('click', () => this.searchEvents());
        document.getElementById('clear-filters').addEventListener('click', () => this.clearFilters());
        document.getElementById('search-keyword').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.searchEvents();
        });

        // Pagination
        document.getElementById('prev-page').addEventListener('click', () => this.changePage(-1));
        document.getElementById('next-page').addEventListener('click', () => this.changePage(1));

        // Create event button
        document.getElementById('create-event-btn').addEventListener('click', () => {
            this.showCreateForm();
        });

        // Form actions
        document.getElementById('event-form').addEventListener('submit', (e) => {
            e.preventDefault();
            this.saveEvent();
        });

        document.getElementById('cancel-btn').addEventListener('click', () => {
            this.showView('events');
        });

        // Participant form
        document.getElementById('participant-form').addEventListener('submit', (e) => {
            e.preventDefault();
            this.registerParticipant();
        });

        // Modal
        document.getElementById('close-modal').addEventListener('click', () => {
            this.closeModal();
        });

        // Click outside modal to close
        document.getElementById('event-modal').addEventListener('click', (e) => {
            if (e.target.id === 'event-modal') {
                this.closeModal();
            }
        });
    }

    // View Management
    switchView(view) {
        this.currentView = view;
        this.showView(view);
        
        // Update navigation
        document.querySelectorAll('.nav-btn').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.view === view);
        });

        // Load data based on view
        switch (view) {
            case 'dashboard':
                this.loadDashboard();
                break;
            case 'events':
                this.loadEvents();
                break;
            case 'create-event':
                this.showCreateForm();
                break;
            case 'statistics':
                this.loadStatistics();
                break;
        }
    }

    showView(view) {
        document.querySelectorAll('.view').forEach(v => {
            v.classList.remove('active');
        });
        document.getElementById(`${view}-view`).classList.add('active');
    }

    // API Calls
    async apiCall(endpoint, options = {}) {
        this.showLoading();
        try {
            const response = await fetch(`${this.apiBase}${endpoint}`, {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                ...options
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            return data;
        } catch (error) {
            console.error('API Error:', error);
            this.showToast('エラーが発生しました: ' + error.message, 'error');
            throw error;
        } finally {
            this.hideLoading();
        }
    }

    // Dashboard
    async loadDashboard() {
        try {
            const [events, statistics] = await Promise.all([
                this.apiCall('/events'),
                this.apiCall('/events/statistics')
            ]);

            this.events = events.data || events;
            this.statistics = statistics.data || statistics;

            this.updateDashboardStats();
            this.showRecentEvents();
        } catch (error) {
            console.error('Failed to load dashboard:', error);
        }
    }

    updateDashboardStats() {
        const stats = this.statistics;
        const activeEvents = this.events.filter(e => e.status === 'ACTIVE').length;
        const upcomingEvents = this.events.filter(e => 
            new Date(e.startDateTime) > new Date() && e.status === 'ACTIVE'
        ).length;

        document.getElementById('total-events').textContent = this.events.length;
        document.getElementById('active-events').textContent = activeEvents;
        document.getElementById('total-participants').textContent = stats.totalParticipants || 0;
        document.getElementById('upcoming-events').textContent = upcomingEvents;
    }

    showRecentEvents() {
        const recentEvents = this.events
            .sort((a, b) => new Date(b.createdAt || b.startDateTime) - new Date(a.createdAt || a.startDateTime))
            .slice(0, 5);

        const container = document.getElementById('recent-events-list');
        container.innerHTML = recentEvents.map(event => this.createEventCard(event)).join('');
    }

    // Events Management
    async loadEvents() {
        try {
            const response = await this.apiCall('/events');
            this.events = response.data || response;
            this.displayEvents();
        } catch (error) {
            console.error('Failed to load events:', error);
        }
    }

    async searchEvents() {
        const keyword = document.getElementById('search-keyword').value;
        const status = document.getElementById('status-filter').value;
        const dateFrom = document.getElementById('date-from').value;
        const dateTo = document.getElementById('date-to').value;

        const searchParams = {
            keyword: keyword || undefined,
            status: status || undefined,
            startDateFrom: dateFrom ? `${dateFrom}T00:00:00` : undefined,
            startDateTo: dateTo ? `${dateTo}T23:59:59` : undefined,
            page: this.currentPage,
            size: this.pageSize,
            sortBy: 'startDateTime',
            sortOrder: 'desc'
        };

        // Remove undefined values
        Object.keys(searchParams).forEach(key => {
            if (searchParams[key] === undefined) {
                delete searchParams[key];
            }
        });

        try {
            const response = await this.apiCall('/events/search', {
                method: 'POST',
                body: JSON.stringify(searchParams)
            });
            this.events = response.data || response.content || response;
            this.displayEvents();
        } catch (error) {
            console.error('Failed to search events:', error);
        }
    }

    clearFilters() {
        document.getElementById('search-keyword').value = '';
        document.getElementById('status-filter').value = '';
        document.getElementById('date-from').value = '';
        document.getElementById('date-to').value = '';
        this.currentPage = 0;
        this.loadEvents();
    }

    displayEvents() {
        const container = document.getElementById('events-list');
        if (this.events.length === 0) {
            container.innerHTML = '<p class="no-events">イベントが見つかりません。</p>';
            return;
        }

        container.innerHTML = this.events.map(event => this.createEventCard(event)).join('');
    }

    createEventCard(event) {
        const startDate = new Date(event.startDateTime);
        const endDate = new Date(event.endDateTime);
        const participantRatio = `${event.currentParticipants || 0}/${event.maxParticipants}`;

        return `
            <div class="event-card">
                <div class="event-header">
                    <h3 class="event-title">${this.escapeHtml(event.eventName)}</h3>
                    <div class="event-meta">
                        <span><i class="fas fa-user"></i> ${this.escapeHtml(event.organizer)}</span>
                        <span><i class="fas fa-users"></i> ${participantRatio}</span>
                        <span class="event-status status-${event.status.toLowerCase()}">${this.getStatusText(event.status)}</span>
                    </div>
                </div>
                <div class="event-body">
                    <p class="event-description">${this.escapeHtml(event.description || '')}</p>
                    <div class="event-details">
                        <div class="event-detail">
                            <i class="fas fa-calendar-alt"></i>
                            <span>${startDate.toLocaleDateString('ja-JP')} ${startDate.toLocaleTimeString('ja-JP', {hour: '2-digit', minute: '2-digit'})}</span>
                        </div>
                        <div class="event-detail">
                            <i class="fas fa-clock"></i>
                            <span>${endDate.toLocaleDateString('ja-JP')} ${endDate.toLocaleTimeString('ja-JP', {hour: '2-digit', minute: '2-digit'})}</span>
                        </div>
                        <div class="event-detail">
                            <i class="fas fa-map-marker-alt"></i>
                            <span>${this.escapeHtml(event.location)}</span>
                        </div>
                    </div>
                    <div class="event-actions">
                        <button class="btn btn-outline" onclick="app.showEventDetails(${event.eventId})">
                            <i class="fas fa-eye"></i> 詳細
                        </button>
                        <button class="btn btn-secondary" onclick="app.editEvent(${event.eventId})">
                            <i class="fas fa-edit"></i> 編集
                        </button>
                        <button class="btn btn-danger" onclick="app.deleteEvent(${event.eventId})">
                            <i class="fas fa-trash"></i> 削除
                        </button>
                    </div>
                </div>
            </div>
        `;
    }

    // Event Actions
    async showEventDetails(eventId) {
        try {
            const event = await this.apiCall(`/events/${eventId}`);
            const participants = await this.apiCall(`/participants/event/${eventId}`);

            this.currentEventId = eventId;
            this.displayEventModal(event, participants);
        } catch (error) {
            console.error('Failed to load event details:', error);
        }
    }

    displayEventModal(event, participants) {
        const startDate = new Date(event.startDateTime);
        const endDate = new Date(event.endDateTime);

        document.getElementById('modal-title').textContent = event.eventName;
        document.getElementById('event-details').innerHTML = `
            <div class="event-detail-section">
                <h4>イベント情報</h4>
                <div class="event-detail-grid">
                    <div class="event-detail">
                        <strong>説明:</strong>
                        <p>${this.escapeHtml(event.description || '')}</p>
                    </div>
                    <div class="event-detail">
                        <strong>開始日時:</strong>
                        <span>${startDate.toLocaleDateString('ja-JP')} ${startDate.toLocaleTimeString('ja-JP')}</span>
                    </div>
                    <div class="event-detail">
                        <strong>終了日時:</strong>
                        <span>${endDate.toLocaleDateString('ja-JP')} ${endDate.toLocaleTimeString('ja-JP')}</span>
                    </div>
                    <div class="event-detail">
                        <strong>場所:</strong>
                        <span>${this.escapeHtml(event.location)}</span>
                    </div>
                    <div class="event-detail">
                        <strong>主催者:</strong>
                        <span>${this.escapeHtml(event.organizer)}</span>
                    </div>
                    <div class="event-detail">
                        <strong>参加者数:</strong>
                        <span>${event.currentParticipants || 0} / ${event.maxParticipants}</span>
                    </div>
                    <div class="event-detail">
                        <strong>ステータス:</strong>
                        <span class="event-status status-${event.status.toLowerCase()}">${this.getStatusText(event.status)}</span>
                    </div>
                </div>
            </div>
        `;

        this.displayParticipants(participants);
        this.showModal();
    }

    displayParticipants(participants) {
        const container = document.getElementById('participants-list');
        if (!participants || participants.length === 0) {
            container.innerHTML = '<p>参加者はいません。</p>';
            return;
        }

        container.innerHTML = participants.map(participant => `
            <div class="participant-item">
                <div class="participant-info">
                    <div class="participant-name">${this.escapeHtml(participant.participantName)}</div>
                    <div class="participant-email">${this.escapeHtml(participant.participantEmail)}</div>
                    ${participant.participantPhone ? `<div class="participant-phone">${this.escapeHtml(participant.participantPhone)}</div>` : ''}
                </div>
                <button class="btn btn-danger btn-sm" onclick="app.removeParticipant(${participant.participationId})">
                    <i class="fas fa-times"></i> 削除
                </button>
            </div>
        `).join('');
    }

    async registerParticipant() {
        const form = document.getElementById('participant-form');
        const formData = new FormData(form);
        
        const participantData = {
            eventId: this.currentEventId,
            participantName: formData.get('participantName'),
            participantEmail: formData.get('participantEmail'),
            participantPhone: formData.get('participantPhone')
        };

        try {
            await this.apiCall('/participants/register', {
                method: 'POST',
                body: JSON.stringify(participantData)
            });

            this.showToast('参加者が正常に登録されました。', 'success');
            form.reset();
            this.showEventDetails(this.currentEventId); // Refresh participant list
        } catch (error) {
            console.error('Failed to register participant:', error);
        }
    }

    async removeParticipant(participationId) {
        if (!confirm('この参加者を削除しますか？')) {
            return;
        }

        try {
            await this.apiCall(`/participants/cancel/${participationId}`, {
                method: 'DELETE'
            });

            this.showToast('参加者が正常に削除されました。', 'success');
            this.showEventDetails(this.currentEventId); // Refresh participant list
        } catch (error) {
            console.error('Failed to remove participant:', error);
        }
    }

    // Event Form
    showCreateForm() {
        document.getElementById('form-title').textContent = '新規イベント作成';
        document.getElementById('event-form').reset();
        this.currentEventId = null;
        this.showView('create-event');
    }

    async editEvent(eventId) {
        try {
            const event = await this.apiCall(`/events/${eventId}`);
            this.populateForm(event);
            document.getElementById('form-title').textContent = 'イベント編集';
            this.currentEventId = eventId;
            this.showView('create-event');
        } catch (error) {
            console.error('Failed to load event for editing:', error);
        }
    }

    populateForm(event) {
        document.getElementById('event-name').value = event.eventName;
        document.getElementById('event-description').value = event.description || '';
        document.getElementById('start-datetime').value = this.formatDateTimeLocal(event.startDateTime);
        document.getElementById('end-datetime').value = this.formatDateTimeLocal(event.endDateTime);
        document.getElementById('location').value = event.location;
        document.getElementById('organizer').value = event.organizer;
        document.getElementById('max-participants').value = event.maxParticipants;
    }

    async saveEvent() {
        const form = document.getElementById('event-form');
        const formData = new FormData(form);
        
        const eventData = {
            eventName: formData.get('eventName'),
            description: formData.get('description'),
            startDateTime: formData.get('startDateTime'),
            endDateTime: formData.get('endDateTime'),
            location: formData.get('location'),
            organizer: formData.get('organizer'),
            maxParticipants: parseInt(formData.get('maxParticipants'))
        };

        try {
            if (this.currentEventId) {
                await this.apiCall(`/events/${this.currentEventId}`, {
                    method: 'PUT',
                    body: JSON.stringify(eventData)
                });
                this.showToast('イベントが正常に更新されました。', 'success');
            } else {
                await this.apiCall('/events', {
                    method: 'POST',
                    body: JSON.stringify(eventData)
                });
                this.showToast('イベントが正常に作成されました。', 'success');
            }

            this.showView('events');
            this.loadEvents();
        } catch (error) {
            console.error('Failed to save event:', error);
        }
    }

    async deleteEvent(eventId) {
        if (!confirm('このイベントを削除しますか？')) {
            return;
        }

        try {
            await this.apiCall(`/events/${eventId}`, {
                method: 'DELETE'
            });

            this.showToast('イベントが正常に削除されました。', 'success');
            this.loadEvents();
        } catch (error) {
            console.error('Failed to delete event:', error);
        }
    }

    // Statistics
    async loadStatistics() {
        try {
            const statistics = await this.apiCall('/events/statistics');
            this.displayStatistics(statistics);
        } catch (error) {
            console.error('Failed to load statistics:', error);
        }
    }

    displayStatistics(statistics) {
        const eventStats = document.getElementById('event-stats');
        const participantStats = document.getElementById('participant-stats');

        eventStats.innerHTML = `
            <div class="stat-item">
                <span class="stat-label">総イベント数</span>
                <span class="stat-value">${statistics.totalEvents || 0}</span>
            </div>
            <div class="stat-item">
                <span class="stat-label">アクティブイベント</span>
                <span class="stat-value">${statistics.activeEvents || 0}</span>
            </div>
            <div class="stat-item">
                <span class="stat-label">完了イベント</span>
                <span class="stat-value">${statistics.completedEvents || 0}</span>
            </div>
            <div class="stat-item">
                <span class="stat-label">キャンセルイベント</span>
                <span class="stat-value">${statistics.cancelledEvents || 0}</span>
            </div>
        `;

        participantStats.innerHTML = `
            <div class="stat-item">
                <span class="stat-label">総参加者数</span>
                <span class="stat-value">${statistics.totalParticipants || 0}</span>
            </div>
            <div class="stat-item">
                <span class="stat-label">平均参加者数</span>
                <span class="stat-value">${statistics.averageParticipants || 0}</span>
            </div>
            <div class="stat-item">
                <span class="stat-label">最大参加者数</span>
                <span class="stat-value">${statistics.maxParticipants || 0}</span>
            </div>
            <div class="stat-item">
                <span class="stat-label">参加率</span>
                <span class="stat-value">${statistics.participationRate || 0}%</span>
            </div>
        `;
    }

    // Pagination
    changePage(direction) {
        this.currentPage += direction;
        if (this.currentPage < 0) this.currentPage = 0;
        
        if (document.getElementById('search-keyword').value || 
            document.getElementById('status-filter').value ||
            document.getElementById('date-from').value ||
            document.getElementById('date-to').value) {
            this.searchEvents();
        } else {
            this.loadEvents();
        }
    }

    // Modal
    showModal() {
        document.getElementById('event-modal').classList.add('active');
    }

    closeModal() {
        document.getElementById('event-modal').classList.remove('active');
        document.getElementById('participant-form').reset();
    }

    // Loading
    showLoading() {
        document.getElementById('loading').classList.add('active');
    }

    hideLoading() {
        document.getElementById('loading').classList.remove('active');
    }

    // Toast Notifications
    showToast(message, type = 'success') {
        const toastContainer = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = message;
        
        toastContainer.appendChild(toast);
        
        setTimeout(() => {
            toast.remove();
        }, 5000);
    }

    // Utility Functions
    escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    formatDateTimeLocal(dateTime) {
        if (!dateTime) return '';
        const date = new Date(dateTime);
        return date.toISOString().slice(0, 16);
    }

    getStatusText(status) {
        const statusMap = {
            'ACTIVE': 'アクティブ',
            'COMPLETED': '完了',
            'CANCELLED': 'キャンセル'
        };
        return statusMap[status] || status;
    }
}

// Initialize the application when the page loads
document.addEventListener('DOMContentLoaded', () => {
    window.app = new EventManagementApp();
});