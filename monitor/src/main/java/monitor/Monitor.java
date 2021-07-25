package monitor;

import monitor.observer.DiagnosticDataPoint;
import monitor.observer.ServiceObserver;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class Monitor {
    private final List<ServiceObserver> serviceObservers;

    public Monitor(List<ServiceObserver> serviceObservers) {
        this.serviceObservers = requireNonNull(serviceObservers);
    }

    public void updateStatistics() {
        List<DiagnosticDataPoint> newData = serviceObservers.stream()
                .map(ServiceObserver::gatherDataFromService)
                .collect(Collectors.toList());
    }
}
