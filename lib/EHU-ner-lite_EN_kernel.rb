module Opener
   module Kernel
     module EHU
       module NER
    	 module Lite
    	   module EN
      		VERSION = "0.0.1"

      		class Configuration
        		CORE_DIR    = File.expand_path("../core", File.dirname(__FILE__))
        		KERNEL_CORE = CORE_DIR+'/ehu-opennlp-nerc-en-1.0.jar'
      		end

    	  end
    	end
      end
    end
  end
end

KERNEL_CORE=Opener::Kernel::EHU::NER::Lite::EN::Configuration::KERNEL_CORE
