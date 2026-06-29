#include "phantom/oracle.hpp"
#include "phantom/governor.hpp"
#include "phantom/cache.hpp"
#include "phantom/download.hpp"
#include "phantom/launch.hpp"
#include <iostream>
#include <memory>
#include <thread>
#include <chrono>
#include <simdjson.h>

using namespace phantom;

int main(int argc, char* argv[]) {
    std::cout << "[Phantom Engine] Booting native core...\n";

    // Initialize module graph
    auto oracle = std::make_shared<HardwareOracle>();
    auto governor = std::make_shared<AdaptiveResourceGovernor>(oracle);
    auto cache = std::make_shared<ZeroWasteCacheEngine>(governor);
    auto download = std::make_shared<PhantomDownloadEngine>(governor);
    auto launch = std::make_shared<PhantomLaunchEngine>(oracle);

    // Bootstrap hardware profile
    HardwareProfile initial_profile{8, 16384, false, false, false}; 
    oracle->initialize(initial_profile);
    
    auto profile = oracle->get_current_profile().value();
    governor->initialize(profile);
    cache->initialize(profile);
    download->initialize(profile);
    launch->initialize(profile);

    std::cout << "[Phantom Engine] Module initialization complete.\n";
    std::cout << "[Phantom Engine] Active Memory Tier: " 
              << static_cast<int>(governor->get_current_tier()) << "\n";
    std::cout << "[Phantom Engine] Active Worker Threads: " 
              << governor->get_active_worker_threads() << "\n";

    // Mock an IPC launch event
    std::cout << "[IPC Bridge] Listening on local pipe...\n";
    std::cout << "[IPC Bridge] Received launch request for instance 'alpha-01'\n";
    
    auto handle = launch->launch_instance(InstanceId{"alpha-01"});

    // Ghost Mode Transition (Phase 4)
    std::cout << "[Phantom Engine] Launch successful. Transitioning to Ghost Mode.\n";
    governor->suspend();
    cache->suspend();
    download->suspend();
    launch->suspend();
    
    std::cout << "[Ghost Mode] Footprint minimized (< 12MB target). Waiting for game exit...\n";
    std::this_thread::sleep_for(std::chrono::milliseconds(200)); // Simulate game running

    std::cout << "[Ghost Mode] Process handle returned. Resuming modules...\n";
    governor->resume();
    cache->resume();
    download->resume();
    launch->resume();

    std::cout << "[Phantom Engine] Shutdown sequence complete.\n";
    return 0;
}
