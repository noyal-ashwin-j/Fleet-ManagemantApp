import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

// ------------------- Vehicle -------------------
abstract class Vehicle {
    private String regNo;
    private String type;
    private double odometer;
    private String status;
    private LocalDate lastServiceDate;

    public Vehicle(String regNo, String type, double odometer, String status, LocalDate lastServiceDate) {
        this.regNo = regNo;
        this.type = type;
        this.odometer = odometer;
        this.status = status;
        this.lastServiceDate = lastServiceDate;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getType() {
        return type;
    }

    public double getOdometer() {
        return odometer;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getLastServiceDate() {
        return lastServiceDate;
    }

    public void updateOdometer(double km) {
        if (km >= 0) {
            this.odometer += km;
        } else {
            System.out.println("Invalid odometer update!");
        }
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void updateLastServiceDate(LocalDate date) {
        this.lastServiceDate = date;
    }

    public abstract LocalDate nextServiceDue();

    public abstract double operatingCostPerKm();

    @Override
    public String toString() {
        return type + " (" + regNo + ") Odometer: " + odometer + " km, Status: " + status;
    }
}

// ------------------- Truck -------------------
class Truck extends Vehicle {
    public Truck(String regNo, double odometer, String status, LocalDate lastServiceDate) {
        super(regNo, "Truck", odometer, status, lastServiceDate);
    }

    @Override
    public LocalDate nextServiceDue() {
        return getLastServiceDate().plusMonths(6);
    }

    @Override
    public double operatingCostPerKm() {
        return 1.5;
    }
}

// ------------------- Car -------------------
class Car extends Vehicle {
    public Car(String regNo, double odometer, String status, LocalDate lastServiceDate) {
        super(regNo, "Car", odometer, status, lastServiceDate);
    }

    @Override
    public LocalDate nextServiceDue() {
        return getLastServiceDate().plusMonths(12);
    }

    @Override
    public double operatingCostPerKm() {
        return 0.8;
    }
}

// ------------------- Bike -------------------
class Bike extends Vehicle {
    public Bike(String regNo, double odometer, String status, LocalDate lastServiceDate) {
        super(regNo, "Bike", odometer, status, lastServiceDate);
    }

    @Override
    public LocalDate nextServiceDue() {
        return getLastServiceDate().plusMonths(3);
    }

    @Override
    public double operatingCostPerKm() {
        return 0.4;
    }
}

// ------------------- ServiceRecord -------------------
class ServiceRecord {
    private String recordId;
    private Vehicle vehicle;
    private String serviceType;
    private double cost;
    private LocalDate date;
    private String notes;

    public ServiceRecord(String recordId, Vehicle vehicle, String serviceType, double cost, LocalDate date, String notes) {
        this.recordId = recordId;
        this.vehicle = vehicle;
        this.serviceType = serviceType;
        this.cost = cost;
        this.date = date;
        this.notes = notes;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public double getCost() {
        return cost;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "ServiceRecord: " + serviceType + " for " + vehicle.getRegNo() + " on " + date + " costing $" + cost;
    }
}

// ------------------- FuelEntry -------------------
class FuelEntry {
    private String entryId;
    private Vehicle vehicle;
    private double liters;
    private double pricePerLiter;
    private double odometer;
    private String station;

    public FuelEntry(String entryId, Vehicle vehicle, double liters, double pricePerLiter, double odometer, String station) {
        this.entryId = entryId;
        this.vehicle = vehicle;
        this.liters = liters;
        this.pricePerLiter = pricePerLiter;
        this.odometer = odometer;
        this.station = station;
    }

    // Overloaded constructor: by total amount only
    public FuelEntry(String entryId, Vehicle vehicle, double totalAmount, double odometer, String station) {
        this(entryId, vehicle, totalAmount / 1.2, 1.2, odometer, station); // Assume default price
    }

    public double getTotalCost() {
        return liters * pricePerLiter;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public double getOdometer() {
        return odometer;
    }

    public LocalDate getDate() {
        return LocalDate.now();
    }

    @Override
    public String toString() {
        return "FuelEntry: " + liters + "L at $" + pricePerLiter + "/L for " + vehicle.getRegNo();
    }
}

// ------------------- FleetService -------------------
class FleetService {
    private List<ServiceRecord> services = new ArrayList<>();
    private List<FuelEntry> fuels = new ArrayList<>();

    public void addService(ServiceRecord record) {
        services.add(record);
    }

    public void logFuel(FuelEntry entry) {
        fuels.add(entry);
    }

    public double computeCostPerKm(Vehicle vehicle) {
        double totalService = services.stream()
                .filter(s -> s.getVehicle().equals(vehicle))
                .mapToDouble(ServiceRecord::getCost)
                .sum();

        double totalFuel = fuels.stream()
                .filter(f -> f.getVehicle().equals(vehicle))
                .mapToDouble(FuelEntry::getTotalCost)
                .sum();

        return vehicle.getOdometer() == 0 ? 0 : (totalService + totalFuel) / vehicle.getOdometer();
    }

    public List<Vehicle> serviceDueAlerts(List<Vehicle> vehicles) {
        LocalDate today = LocalDate.now();
        List<Vehicle> due = new ArrayList<>();
        for (Vehicle v : vehicles) {
            if (today.isAfter(v.nextServiceDue())) {
                due.add(v);
            }
        }
        return due;
    }

    public void printUptimeReport(List<Vehicle> vehicles) {
        System.out.println("\nUptime Report:");
        for (Vehicle v : vehicles) {
            long days = ChronoUnit.DAYS.between(v.getLastServiceDate(), LocalDate.now());
            System.out.println(v.getRegNo() + ": " + days + " days since last service.");
        }
    }

    public void printUtilizationReport(List<Vehicle> vehicles) {
        System.out.println("\nUtilization Report:");
        for (Vehicle v : vehicles) {
            System.out.printf("%s: %.2f km travelled, Operating cost per km: $%.2f\n",
                    v.getRegNo(), v.getOdometer(), v.operatingCostPerKm());
        }
    }

    public void printMonthlyFuelExpenseReport() {
        System.out.println("\nMonthly Fuel Expense Report:");
        Map<String, Double> map = new HashMap<>();
        for (FuelEntry f : fuels) {
            String month = f.getDate().getMonth().toString();
            map.put(month, map.getOrDefault(month, 0.0) + f.getTotalCost());
        }
        if(map.isEmpty()){
            System.out.println("No fuel data available.");
            return;
        }
        for (String month : map.keySet()) {
            System.out.printf("%s: $%.2f\n", month, map.get(month));
        }
    }
}

// ------------------- FleetAppMain -------------------
public class FleetAppMain {
    public static void main(String[] args) {
        FleetService fleetService = new FleetService();
        List<Vehicle> vehicles = new ArrayList<>();

        // Register vehicles
        Vehicle truck = new Truck("TRK123", 5000, "Active", LocalDate.now().minusMonths(7));
        Vehicle car = new Car("CAR456", 12000, "Active", LocalDate.now().minusMonths(10));
        Vehicle bike = new Bike("BIK789", 3000, "Active", LocalDate.now().minusMonths(4));

        vehicles.add(truck);
        vehicles.add(car);
        vehicles.add(bike);

        // Add service records
        fleetService.addService(new ServiceRecord("SR001", truck, "Engine Check", 500, LocalDate.now().minusMonths(7), "Routine"));
        fleetService.addService(new ServiceRecord("SR002", car, "Oil Change", 150, LocalDate.now().minusMonths(10), "Monthly"));
        fleetService.addService(new ServiceRecord("SR003", bike, "Brake Repair", 80, LocalDate.now().minusMonths(4), "Wear"));

        // Add fuel entries
        fleetService.logFuel(new FuelEntry("FE001", truck, 200, 1.5, 5000, "FuelStation1"));
        fleetService.logFuel(new FuelEntry("FE002", car, 50, 1.4, 12000, "FuelStation2"));
        fleetService.logFuel(new FuelEntry("FE003", bike, 20, 1.3, 3000, "FuelStation3"));

        // Compute cost per km
        System.out.println("\nCost per Km Report:");
        for (Vehicle v : vehicles) {
            double cost = fleetService.computeCostPerKm(v);
            System.out.printf("%s: $%.2f per km\n", v.getRegNo(), cost);
        }

        // Service due alerts
        List<Vehicle> due = fleetService.serviceDueAlerts(vehicles);
        System.out.println("\nService Due Alerts:");
        for (Vehicle v : due) {
            System.out.println(v.getRegNo() + " is due for service!");
        }

        // Reports
        fleetService.printUptimeReport(vehicles);
        fleetService.printUtilizationReport(vehicles);
        fleetService.printMonthlyFuelExpenseReport();
    }
}
